# Multi-stage Dockerfile for CGI Personnel & Asset Tracker
# Stage 1: Build JAR using Maven and OpenJDK
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copy Maven files
COPY pom.xml .
COPY src src

# Build the application, skipping tests
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -q

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for OpenShift security context
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy JAR from builder stage
COPY --from=builder /build/target/personnel-tracker-*.jar app.jar

# Set ownership to appuser
RUN chown -R appuser:appgroup /app && \
    chmod -R g+rwX /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=45s --retries=3 \
    CMD wget -q -O- http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
