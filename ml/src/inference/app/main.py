from __future__ import annotations

from fastapi import FastAPI

from .schemas import ScoreResponse, TransactionEvent
from .service import RiskScoringService

app = FastAPI(
    title="AI Fraud Detection Inference",
    version="1.0.0",
    description="Provides heuristic fraud risk scoring for transaction events.",
)

_scoring_service = RiskScoringService()


@app.get("/health")
async def health_check():
    """Health check endpoint for container orchestration"""
    return {
        "status": "healthy",
        "service": "fraud-detection-inference",
        "version": "1.0.0"
    }


@app.post("/v1/score", response_model=ScoreResponse, summary="Score a transaction for fraud risk")
async def score_transaction(event: TransactionEvent) -> ScoreResponse:
    """Compute a risk score for the supplied transaction."""
    return _scoring_service.score(event)
