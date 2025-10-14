# Inference Service

This FastAPI microservice exposes a lightweight fraud risk scoring endpoint used by the `fraud-service`.

## Features

- Parses incoming transaction events and engineers derived features.
- Applies a calibrated logistic scoring function to estimate fraud probability.
- Returns both the risk score (0-1) and a human-readable rationale summarizing key contributing factors.
- Shares feature engineering and model parameter utilities with other ML workflows under `src/common`.
- Ready for containerization via Uvicorn with standard ASGI settings.

## Architecture overview

- `app/service.py` instantiates `RiskScoringService`, which loads heuristic weights from `common.model_io` and derives feature vectors via `common.features`.
- Model artifacts live in `../common/model_artifacts`, making it easy to plug in newly trained weights without modifying code.
- The FastAPI router in `app/main.py` wires the service to the `/v1/score` endpoint and surfaces the latest model version metadata.

## Quick start

```bash
python -m venv .venv
source .venv/bin/activate  # On Windows use `.venv\Scripts\activate`
pip install -r requirements.txt
uvicorn app.main:app --reload --port 9090
```

Interact with the service via the automatically generated Swagger UI at <http://localhost:9090/docs>.

## API

- `POST /v1/score`
  - **Request body**: Transaction payload matching the schema below
  - **Response**: JSON object containing `score` (float 0-1), `rationale` (string), `modelVersion`, and `featureContributions` (object)

Request schema excerpt:

```json
{
  "transactionId": "txn-123",
  "accountId": "acct-42",
  "amount": 542.12,
  "currency": "USD",
  "timestamp": "2024-01-01T12:34:56Z",
  "deviceId": "device-9",
  "location": {
    "latitude": 40.7128,
    "longitude": -74.006
  },
  "merchantCategory": "electronics",
  "metadata": {
    "ipAddress": "203.0.113.7"
  }
}
```

The endpoint responds with:

```json
{
  "score": 0.82,
  "rationale": "High amount, first-time device usage",
  "modelVersion": "heuristic-v1",
  "featureContributions": {
    "amount": 0.62,
    "device_risk": 0.15,
    "geo_distance": 0.05
  }
}
```

## Testing

```bash
# Windows PowerShell
$env:PYTHONPATH="C:\\Users\\patel\\AiFraudDetection\\ml\\src;C:\\Users\\patel\\AiFraudDetection\\ml\\src\\inference"
python -m pytest src/inference/tests/test_app.py

# macOS/Linux
PYTHONPATH="$(pwd)/src:$(pwd)/src/inference" python -m pytest src/inference/tests/test_app.py
```

Pytest exercises both the feature engineering logic and the FastAPI route through the TestClient. Setting `PYTHONPATH` ensures the shared `common` package resolves correctly during test execution.
