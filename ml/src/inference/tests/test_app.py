from __future__ import annotations

from datetime import datetime, timezone

from fastapi.testclient import TestClient

from app.main import app
from app.service import RiskScoringService

client = TestClient(app)


def _sample_payload(**overrides):
    payload = {
        "transactionId": "txn-789",
        "accountId": "acct-100",
        "amount": 1250.50,
        "currency": "USD",
        "createdAt": datetime(2024, 1, 5, 14, 30, tzinfo=timezone.utc).isoformat(),
        "deviceId": "device-12",
        "merchantCategory": "electronics",
        "channel": "card-present",
        "location": {"latitude": 37.7749, "longitude": -122.4194},
        "metadata": {
            "previous_device_id": "device-1",
            "velocity_last_hour": 6,
            "home_lat": "34.0522",
            "home_lon": "-118.2437",
        },
    }
    payload.update(overrides)
    return payload


def test_score_endpoint_returns_probability_and_rationale():
    response = client.post("/v1/score", json=_sample_payload())
    assert response.status_code == 200
    body = response.json()
    assert 0.0 <= body["score"] <= 1.0
    assert "rationale" in body
    assert body["modelVersion"] == RiskScoringService().model_version


def test_velocity_and_geo_increase_risk():
    low_risk_payload = _sample_payload(amount=50.0, metadata={})
    high_risk_payload = _sample_payload(amount=5000.0)

    low_response = client.post("/v1/score", json=low_risk_payload).json()
    high_response = client.post("/v1/score", json=high_risk_payload).json()

    assert high_response["score"] > low_response["score"]


def test_missing_location_is_handled_gracefully():
    payload = _sample_payload(location=None)
    response = client.post("/v1/score", json=payload)
    assert response.status_code == 200
    assert response.json()["score"] >= 0.0
