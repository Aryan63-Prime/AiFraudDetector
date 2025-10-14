from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path
from typing import Dict

DEFAULT_ARTIFACT_PATH = Path(__file__).resolve().parent / "model_artifacts" / "latest_model.json"


@dataclass(frozen=True)
class ModelParameters:
    version: str
    bias: float
    weights: Dict[str, float]

    def predict_logit(self, features: Dict[str, float]) -> float:
        return self.bias + sum(self.weights.get(name, 0.0) * value for name, value in features.items())


def load_model_parameters(path: Path | None = None) -> ModelParameters:
    artifact_path = path or DEFAULT_ARTIFACT_PATH
    with artifact_path.open("r", encoding="utf-8") as handle:
        payload = json.load(handle)

    return ModelParameters(
        version=payload["version"],
        bias=float(payload["bias"]),
        weights={name: float(value) for name, value in payload["weights"].items()},
    )


__all__ = ["ModelParameters", "load_model_parameters", "DEFAULT_ARTIFACT_PATH"]
