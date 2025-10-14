from __future__ import annotations

import math
from typing import Dict

from common.features import FeatureVector, compute_feature_vector
from common.model_io import ModelParameters, load_model_parameters

from .schemas import FeatureSummary, ScoreResponse, TransactionEvent


_FALLBACK_PARAMETERS = ModelParameters(
    version="heuristic-v1",
    bias=-1.25,
    weights={
        "amount": 1.6,
        "device_risk": 1.1,
        "geo_distance": 0.9,
        "velocity": 1.3,
    },
)


class RiskScoringService:
    """Applies a data-driven (or heuristic fallback) fraud risk model."""

    def __init__(self, model: ModelParameters | None = None) -> None:
        if model is not None:
            self._model = model
        else:
            self._model = self._load_model()

    @property
    def model_version(self) -> str:
        return self._model.version

    def score(self, event: TransactionEvent) -> ScoreResponse:
        features = self._extract_features(event)
        feature_dict = features.to_dict()
        raw_score = self._model.predict_logit(feature_dict)
        probability = self._sigmoid(raw_score)
        rationale = self._build_rationale(features, probability)
        return ScoreResponse(
            score=round(probability, 4),
            rationale=rationale,
            modelVersion=self.model_version,
            featureContributions=self._to_summary(features),
        )

    def _extract_features(self, event: TransactionEvent) -> FeatureVector:
        metadata_mapping: Dict[str, object] | None
        if isinstance(event.metadata, Dict):
            metadata_mapping = event.metadata
        elif event.metadata is not None:
            metadata_mapping = {
                **event.metadata.model_dump(by_alias=True, exclude_none=True),
                **event.metadata.extra,
            }
        else:
            metadata_mapping = None

        return compute_feature_vector(
            amount=event.amount,
            device_id=event.deviceId,
            metadata=metadata_mapping,
            location={
                "latitude": event.location.latitude,
                "longitude": event.location.longitude,
            } if event.location else None,
        )

    @staticmethod
    def _sigmoid(value: float) -> float:
        clipped = max(min(value, 10), -10)
        return 1.0 / (1.0 + math.exp(-clipped))

    def _build_rationale(self, features: FeatureVector, probability: float) -> str:
        contributors = []
        if features.amount >= 0.5:
            contributors.append("high transaction amount")
        if features.device_risk >= 0.7:
            contributors.append("unseen device")
        elif features.device_risk >= 0.4:
            contributors.append("device mismatch")
        if features.velocity >= 0.5:
            contributors.append("unusual velocity")
        if features.geo_distance >= 0.5:
            contributors.append("large geo deviation")

        if contributors:
            detail = ", ".join(contributors)
            return f"Risk driven by {detail}; probability={probability:.2f}."
        return f"Low risk indicators; probability={probability:.2f}."

    def _to_summary(self, features: FeatureVector) -> FeatureSummary:
        summary = features.to_dict()
        return FeatureSummary(
            amount=round(summary["amount"], 4),
            device_risk=round(summary["device_risk"], 4),
            geo_distance=round(summary["geo_distance"], 4),
            velocity=round(summary["velocity"], 4),
        )

    @staticmethod
    def _load_model() -> ModelParameters:
        try:
            return load_model_parameters()
        except FileNotFoundError:
            return _FALLBACK_PARAMETERS


__all__ = ["RiskScoringService"]
