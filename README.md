# AI Fraud Detection Platform

This repository hosts a full-stack AI-powered fraud detection system built primarily with Java microservices and Python-based machine learning workflows. The project is organized to support distributed development across ingestion, scoring, alerting, and visualization layers.

## Repository layout

```
.
├── backend/                # Gradle multi-module workspace for Java services
│   ├── build.gradle.kts    # Root Gradle configuration
│   ├── settings.gradle.kts # Module registry
│   ├── common/             # Shared Kotlin/JVM utilities (DTOs, configs)
│   └── services/           # Microservices (Spring Boot)
│       ├── gateway-service/        # Edge entrypoint / health checks
│       ├── transaction-service/    # REST ingestion + transactional store
│       ├── fraud-service/          # Kafka consumer, scoring, decision publisher
│       ├── alert-service/          # Decision persistence + analyst workflow API
│       └── admin-service/          # Admin-facing orchestration / alert UI backend
├── ml/                     # Python ML assets
│   ├── notebooks/          # Exploratory data analysis & experimentation
│   ├── data/               # Raw & processed datasets (gitignored)
│   └── src/
│       ├── common/         # Shared feature tooling & utils
│       ├── training/       # Training pipelines, model registry integration
│       └── inference/      # Serving/inference service & clients
├── ui/
│   └── frontend/           # Web dashboard (React/Angular/Vue)
├── infrastructure/
│   ├── docker/             # Dockerfiles & docker-compose stack
│   ├── k8s/                # Kubernetes manifests / Helm charts
│   └── terraform/          # Infrastructure as Code modules
└── docs/
    └── architecture/       # System design docs, ADRs, diagrams
```

## Build prerequisites

| Stack | Tooling |
| --- | --- |
| Java services | JDK 21, Gradle 8.x (or wrapper), Docker Desktop |
| Python ML | Python 3.11, Poetry/pipenv, virtualenv |
| Frontend | Node.js 20+, PNPM/Yarn |
| Data infra | PostgreSQL 15+, Apache Kafka 3+ |

### Bootstrapping the Java backend

1. Install the matching JDK (Temurin 21 recommended) and ensure `java -version` reports 21.x.
2. If Gradle is available, generate the project wrapper once:
    ```powershell
    cd backend
    gradle wrapper --gradle-version 8.9
    ```
    This produces `gradlew`/`gradlew.bat` and pins the toolchain for collaborators.
3. Build all modules:
    ```powershell
    cd backend
    .\gradlew build
    ```
    > On macOS/Linux use `./gradlew build`.
4. Start individual services. Example for the gateway:
    ```powershell
    .\gradlew :services:gateway-service:bootRun
    ```
5. The transaction service requires PostgreSQL and Kafka:
    ```powershell
    .\gradlew :services:transaction-service:bootRun
    ```
    Configure connection details via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_KAFKA_BOOTSTRAP_SERVERS` (defaults assume local Postgres on port 5432 and Kafka on 9092).
6. The fraud service consumes the `transactions.raw` topic and publishes to `fraud.decisions`:
    ```powershell
    .\gradlew :services:fraud-service:bootRun
    ```
    Ensure Kafka is reachable and configure topic overrides with `KAFKA_TOPIC_TRANSACTIONS_RAW` / `KAFKA_TOPIC_FRAUD_DECISIONS` if you use non-default names.
7. The alert service persists fraud decisions and exposes analyst endpoints:
    ```powershell
    .\gradlew :services:alert-service:bootRun
    ```
    Database settings default to `jdbc:postgresql://localhost:5432/alerts` with user `alert_user`. Override via standard Spring datasource properties. The service consumes Kafka topic `fraud.decisions`; customize with `KAFKA_TOPIC_FRAUD_DECISIONS`.
8. The admin service proxies alert workflows for the UI:
    ```powershell
    .\gradlew :services:admin-service:bootRun
    ```
    Configure the downstream alert API with `ALERT_SERVICE_URL` (defaults to `http://localhost:8083`).

### Running tests

- `./gradlew test` executes all Java unit/integration tests.
- `./gradlew :services:fraud-service:test` (etc.) targets a specific service.
- Python tests are available under `ml/src/inference` via `pytest` once the virtual environment is activated.

## Continuous integration

Automated checks run on every push and pull request via the GitHub Actions workflow in `.github/workflows/ci.yml`. The pipeline performs:

- A full Gradle `test` run across all backend services with Temurin JDK 21.
- Python dependency installation followed by `pytest` for the FastAPI inference service.
- A Docker Compose smoke test that builds the support stack (PostgreSQL, Kafka, inference API) and verifies the inference endpoint is reachable.

All workflow steps are orchestrated on `ubuntu-latest`; the compose stack is always torn down at the end of the job to keep runners clean.

### Fraud scoring inference service (Python)

The `ml/src/inference` package contains a FastAPI microservice that scores transactions.

```powershell
cd ml\src\inference
..\..\..\.venv\Scripts\python.exe -m uvicorn app.main:app --reload --port 9090
```

The service exposes `POST /v1/score` and returns a probability, rationale, model version, and feature contributions.

To delegate fraud scoring to this service, enable the HTTP client in `fraud-service`:

- Set `FRAUD_SCORING_HTTP_ENABLED=true` (or property `fraud.scoring.http.enabled=true`).
- Optionally override `FRAUD_SCORING_HTTP_BASE_URL` and `FRAUD_SCORING_HTTP_TIMEOUT`.

When disabled, the fraud-service falls back to the built-in heuristic scorer.

### Service topology

1. **transaction-service** writes incoming transactions to Postgres and publishes raw events to `transactions.raw` (Kafka).
2. **fraud-service** consumes the raw stream, scores each event (locally or via the HTTP inference service), and emits decisions to `fraud.decisions`.
3. **alert-service** listens for decisions, maintains the `fraud_alerts` table, and provides REST endpoints for alert review.
4. **admin-service** offers admin-facing APIs and proxies requests from the UI to the alert-service.
5. **gateway-service** stays as a lightweight entrypoint/health surface.

### Local supporting services

Use the curated Docker Compose stack to spin up Kafka, PostgreSQL, the inference API, and Kafka UI in one command:

```powershell
cd infrastructure\docker
docker compose up -d
```

The stack publishes the following endpoints to your host machine:

| Service | Host/Port | Notes |
| --- | --- | --- |
| PostgreSQL | `localhost:5432` | Databases `transactions`, `alerts`; users `transaction_user` and `alert_user` share password `change_me`. |
| Kafka broker | `localhost:9092` | Internal listener `kafka:29092` for inter-container traffic. |
| Kafka UI | <http://localhost:8088> | Inspect topics (`transactions.raw`, `fraud.decisions`). |
| Inference API | <http://localhost:9090/docs> | FastAPI swagger for `/v1/score`. |

Shut everything down with:

```powershell
docker compose down -v
```

> **Tip:** The Java services can keep using the default `localhost` connection strings because the compose stack publishes each dependency on the host network ports listed above.

## Next steps

- Flesh out the frontend dashboard to consume the admin-service endpoints.
- Add end-to-end integration tests that exercise the Kafka pipeline against the compose stack.
- Track architecture decisions in `docs/architecture/` and expand service-specific runbooks.
