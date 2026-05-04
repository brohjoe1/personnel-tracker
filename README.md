# CGI Personnel & Asset Tracker

![Build and Deploy to OpenShift](https://github.com/brohjoe1/personnel-tracker/actions/workflows/deploy.yaml/badge.svg)

A Spring Boot REST API simulating a government personnel and equipment tracking system — the kind of data management platform built for DoD and federal agency clients.

## Tech Stack
- Java 17 + Spring Boot 3.2
- Spring Data JPA / Hibernate
- H2 in-memory database (swap for PostgreSQL in production)
- Apache Kafka event streaming
- Spring Actuator (health/readiness probes)
- Deployed on Red Hat OpenShift Service on AWS

## Architecture
Two core entities with a one-to-many relationship:
- **Personnel** — military personnel records with rank, unit, and email
- **Asset** — equipment items (weapons, vehicles, comms, gear) with status lifecycle

Asset status changes (ASSIGNED → UNASSIGNED → MAINTENANCE) publish Kafka events to the `asset-events` topic, consumed by a downstream listener.

## API Endpoints

### Personnel
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/personnel | Get all personnel |
| GET | /api/personnel/{id} | Get by ID |
| GET | /api/personnel?unit=X | Filter by unit |
| GET | /api/personnel?rank=X | Filter by rank |
| GET | /api/personnel/with-assets | Personnel with assigned assets |
| POST | /api/personnel | Create personnel |
| PUT | /api/personnel/{id} | Update personnel |
| PATCH | /api/personnel/{id}/deactivate | Soft delete |
| DELETE | /api/personnel/{id} | Hard delete |

### Assets
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/assets | Get all assets |
| GET | /api/assets/{id} | Get by ID |
| GET | /api/assets/unassigned | Get unassigned assets |
| GET | /api/assets?status=X | Filter by status |
| GET | /api/assets?category=X | Filter by category |
| GET | /api/assets/personnel/{id} | Assets for a person |
| POST | /api/assets | Create asset |
| PUT | /api/assets/{id} | Update asset |
| PATCH | /api/assets/{id}/assign/{personnelId} | Assign to personnel |
| PATCH | /api/assets/{id}/unassign | Unassign |
| PATCH | /api/assets/{id}/maintenance | Send to maintenance |
| DELETE | /api/assets/{id} | Delete asset |

### Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /actuator/health | OpenShift liveness/readiness probe |

## Running Locally

### Quick Start
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080` with an in-memory H2 database. Note: Kafka is optional locally and will gracefully degrade if not available.

### Local Development with Docker Compose

For local development with Kafka enabled, use the provided Docker Compose setup:

```bash
# Start Kafka locally (optional)
docker run -d --name kafka \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_LOG_DIRS=/tmp/kraft-combined-logs \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  -p 9092:9092 apache/kafka:3.7.0

# Run the application with Kafka enabled
mvn spring-boot:run -Dspring.kafka.bootstrap-servers=localhost:9092
```

Stop Kafka:
```bash
docker stop kafka && docker rm kafka
```

### Building Locally

```bash
# Build the JAR
mvn clean package

# Build the Docker image
docker build -t personnel-tracker .

# Run the container
docker run -p 8080:8080 personnel-tracker
```

Access the health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

---

## CI/CD Pipeline

The project uses **GitHub Actions** to automatically build, containerize, and deploy the application to OpenShift on every push to the `main` branch.

### Pipeline Architecture

```
Push to main
    ↓
┌─────────────────────────────────────┐
│ Stage 1: Build & Test               │
│ - Checkout code                     │
│ - Set up Java 17 & Maven            │
│ - Run mvn clean package             │
│ - Upload JAR artifact               │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Stage 2: Build & Push Docker Image  │
│ - Log in to ghcr.io                 │
│ - Build multi-stage Docker image    │
│ - Push to GitHub Container Registry │
│   (tags: latest, git-sha)           │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Stage 3: Deploy to OpenShift        │
│ - Authenticate with OPENSHIFT_TOKEN │
│ - Apply K8s manifests               │
│ - Update deployment image SHA       │
│ - Wait for rollout to complete      │
└─────────────────────────────────────┘
    ↓
 ✓ Live on OpenShift
```

### GitHub Secrets Required

Add the following secret to GitHub Actions:

| Secret | Value | Source |
|--------|-------|--------|
| `OPENSHIFT_TOKEN` | OpenShift service account token (valid 1 year) | See Deployment section |
| `GITHUB_TOKEN` | Automatically provided by GitHub Actions | No setup needed |

### Image Registry

Images are pushed to **GitHub Container Registry (GHCR)**:
- `ghcr.io/brohjoe1/personnel-tracker:latest` — Latest build
- `ghcr.io/brohjoe1/personnel-tracker:<git-sha>` — Immutable release tag

Note: Ensure the package visibility is **public** in GitHub Settings → Packages and Publishing, or configure OpenShift pull secrets.

---

## Deployment

### Prerequisites

- Access to Red Hat OpenShift cluster: https://api.rm2.thpm.p1.openshiftapps.com:6443
- Namespace: `brohjoe1-dev` (should already exist)
- `oc` CLI installed (https://mirror.openshift.com/pub/openshift-v4/clients/ocp/)

### One-Time Setup: Create OpenShift Service Account

Before the first deployment, create a service account that GitHub Actions will use to authenticate:

```bash
bash scripts/setup-openshift-sa.sh
```

This script:
1. Creates a `github-actions-sa` service account in the `brohjoe1-dev` namespace
2. Grants it the `edit` role for deployments
3. Generates a token valid for 1 year
4. Prints instructions for adding the token to GitHub

**Then add the token to GitHub:**
1. Go to: https://github.com/brohjoe1/personnel-tracker/settings/secrets/actions
2. Click "New repository secret"
3. Name: `OPENSHIFT_TOKEN`
4. Value: [paste the token from the script output]
5. Click "Add secret"

### Deployment Flow

Once the service account is set up, deployments happen automatically:

1. **Push to main** → GitHub Actions workflow triggers
2. **Build & Test** → Runs Maven build with JUnit tests
3. **Build & Push** → Docker image pushed to ghcr.io
4. **Deploy** → OpenShift deployment rolls out with new image
5. **Monitor** → Workflow waits for rollout to complete

### Manual Deployment

To manually deploy without pushing to main:

```bash
# Authenticate to OpenShift
oc login --token=<your-token> --server=https://api.rm2.thpm.p1.openshiftapps.com:6443

# Switch to project
oc project brohjoe1-dev

# Apply all manifests
oc apply -f k8s/

# Watch rollout
oc rollout status deployment/personnel-tracker

# Check running pods
oc get pods -l app=personnel-tracker

# View logs
oc logs -f deployment/personnel-tracker

# Get route URL
oc get route personnel-tracker
```

### Accessing the Application

After deployment, the application is accessible at:
```
https://personnel-tracker-brohjoe1-dev.apps.rm2.thpm.p1.openshiftapps.com
```

Health check endpoint:
```
https://personnel-tracker-brohjoe1-dev.apps.rm2.thpm.p1.openshiftapps.com/actuator/health
```

### Troubleshooting

**Deployment stuck in pending:**
```bash
oc describe pod <pod-name> -n brohjoe1-dev
```

**Check deployment logs:**
```bash
oc logs deployment/personnel-tracker -n brohjoe1-dev --tail=50
```

**Verify Kafka connectivity:**
```bash
oc logs deployment/personnel-tracker -n brohjoe1-dev | grep -i kafka
```

**Restart deployment:**
```bash
oc rollout restart deployment/personnel-tracker -n brohjoe1-dev
```

---

## Kafka Events
Asset lifecycle changes publish to topic `asset-events`:
```json
{
  "assetId": 1,
  "serialNumber": "SN-M4-00142",
  "assetName": "M4 Carbine",
  "action": "ASSIGNED",
  "personnelId": 3,
  "personnelName": "Kevin Wallace",
  "timestamp": "2026-05-04T15:27:43"
}
```
