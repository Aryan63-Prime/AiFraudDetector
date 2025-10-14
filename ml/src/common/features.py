from __future__ import annotations

import math
from dataclasses import dataclass
from typing import Mapping, Any, Optional


@dataclass(frozen=True)
class FeatureVector:
    """Normalized feature representation shared across training and inference."""

    amount: float
    device_risk: float
    geo_distance: float
    velocity: float

    def to_dict(self) -> dict[str, float]:
        return {
            "amount": round(self.amount, 6),
            "device_risk": round(self.device_risk, 6),
            "geo_distance": round(self.geo_distance, 6),
            "velocity": round(self.velocity, 6),
        }


def compute_feature_vector(
    *,
    amount: float,
    device_id: Optional[str],
    metadata: Optional[Mapping[str, Any]] = None,
    location: Optional[Mapping[str, float]] = None,
) -> FeatureVector:
    """Replicates the feature engineering used by the inference service.

    Args:
        amount: Raw transaction amount in the payment currency.
        device_id: Unique device identifier, if available.
        metadata: Optional additional attributes. Keys such as
            ``previous_device_id`` / ``previousDeviceId`` and
            ``velocity_last_hour`` / ``velocityLastHour`` are consumed when present.
        location: Mapping with ``latitude`` and ``longitude`` keys. May be missing.

    Returns:
        FeatureVector with values in the 0-1 range where appropriate.
    """

    amount_norm = min(max(amount, 0.0) / 5000.0, 1.0)

    previous_device = None
    velocity_raw = 0.0
    metadata_extra: Mapping[str, Any] = metadata or {}

    if metadata_extra:
        previous_device = _first_present(metadata_extra, [
            "previous_device_id",
            "previousDeviceId",
        ])
        velocity_raw = float(
            _first_present(metadata_extra, [
                "velocity_last_hour",
                "velocityLastHour",
            ])
            or 0.0
        )

    device_risk = 1.0 if not device_id or device_id == previous_device else 0.4 if previous_device else 0.7
    velocity_norm = min(max(velocity_raw, 0.0) / 10.0, 1.0)

    geo_distance = 0.0
    if location and _has_lat_lon(location) and metadata_extra:
        home_lat = _coerce_float(metadata_extra.get("home_lat") or metadata_extra.get("homeLat"))
        home_lon = _coerce_float(metadata_extra.get("home_lon") or metadata_extra.get("homeLon"))
        if home_lat is not None and home_lon is not None:
            geo_distance = min(
                _haversine_distance(
                    float(location["latitude"]),
                    float(location["longitude"]),
                    home_lat,
                    home_lon,
                )
                / 5000.0,
                1.0,
            )
        else:
            geo_distance = 0.3
    elif location is None:
        geo_distance = 0.2

    return FeatureVector(
        amount=amount_norm,
        device_risk=device_risk,
        geo_distance=geo_distance,
        velocity=velocity_norm,
    )


def _first_present(metadata: Mapping[str, Any], keys: list[str]) -> Optional[Any]:
    for key in keys:
        if key in metadata:
            return metadata[key]
    return None


def _coerce_float(value: Any) -> Optional[float]:
    if value is None:
        return None
    try:
        return float(value)
    except (TypeError, ValueError):
        return None


def _has_lat_lon(location: Mapping[str, Any]) -> bool:
    return "latitude" in location and "longitude" in location


def _haversine_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    radius_earth_km = 6371.0
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    d_phi = math.radians(lat2 - lat1)
    d_lambda = math.radians(lon2 - lon1)

    a = math.sin(d_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(d_lambda / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return radius_earth_km * c


__all__ = ["FeatureVector", "compute_feature_vector"]
