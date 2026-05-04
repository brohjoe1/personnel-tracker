# CGI Personnel & Asset Tracker

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
```bash
mvn spring-boot:run
```
Requires a Kafka broker at localhost:9092. To run without Kafka, set:
```
spring.kafka.admin.fail-fast=false
```

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
