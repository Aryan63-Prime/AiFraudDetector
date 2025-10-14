# Alert Service Overview

The alert-service ingests fraud decisions, persists them for analyst review, and exposes REST APIs for case management. This document captures the runtime dependencies, message contracts, and configuration toggles.

## Responsibilities

1. Consume `fraud.decisions` events produced by the fraud-service.
2. Upsert alert records in the `fraud_alerts` Postgres table, maintaining lifecycle timestamps and status.
3. Offer REST endpoints for listing and updating alerts, consumed by the admin-service / UI layer.

## Runtime Dependencies

| Component | Purpose | Default | Configuration |
| --- | --- | --- | --- |
| PostgreSQL | Persistent storage for alerts | `jdbc:postgresql://localhost:5432/alerts` | Override via Spring datasource properties (`SPRING_DATASOURCE_*`). |
| Kafka | Decision ingestion (`fraud.decisions`) | `localhost:9092` | `SPRING_KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_TOPIC_FRAUD_DECISIONS` |

The service validates the schema at startup (`spring.jpa.hibernate.ddl-auto=validate`); ensure Flyway or manual DDL is applied before deployment.

## External API

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/v1/alerts` | Paged listing with optional status filter (`status=OPEN`/`ACKNOWLEDGED`/`DISMISSED`). |
| `PATCH` | `/api/v1/alerts/{id}/status` | Update alert status using payload `{ "status": "ACKNOWLEDGED" }`. |

Responses are serialized via `AlertResponse`, containing metadata such as `riskScore`, `recommendation`, `evaluatedAt`, and timestamps.

## Event Contract

The Kafka consumer expects JSON payloads shaped as:

```json
{
  "transactionId": "txn-123",
  "riskScore": 0.92,
  "riskLevel": "HIGH",
  "recommendation": "BLOCK",
  "evaluatedAt": "2024-01-01T12:30:00Z"
}
```

Unknown fields are ignored gracefully, allowing forward-compatible schema evolution.

## Integration with Admin Service

The admin-service issues HTTP requests to the alert-service using the following configuration:

- `ALERT_SERVICE_URL` (defaults to `http://localhost:8083`).
- `alerts.gateway.timeout` controls both connect and read timeouts (default 5s).

Ensure the admin-service runs with network reachability to the alert-service for listing and status updates.
