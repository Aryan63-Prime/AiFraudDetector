from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, Optional

from pydantic import BaseModel, ConfigDict, Field


class GeoPoint(BaseModel):
    latitude: float = Field(..., ge=-90.0, le=90.0)
    longitude: float = Field(..., ge=-180.0, le=180.0)


class TransactionMetadata(BaseModel):
    ipAddress: Optional[str] = Field(None, alias="ip_address")
    userAgent: Optional[str] = Field(None, alias="user_agent")
    previousDeviceId: Optional[str] = Field(None, alias="previous_device_id")
    velocityLastHour: Optional[int] = Field(
        None,
        alias="velocity_last_hour",
        description="Number of transactions observed for this account in the past hour.",
    )
    extra: Dict[str, str] = Field(default_factory=dict)
    model_config = ConfigDict(populate_by_name=True, extra="allow")


class TransactionEvent(BaseModel):
    transactionId: str = Field(..., min_length=5, max_length=64)
    accountId: str = Field(..., min_length=3, max_length=64)
    amount: float = Field(..., ge=0)
    currency: str = Field(..., min_length=3, max_length=3)
    createdAt: datetime
    deviceId: Optional[str] = Field(None, min_length=3, max_length=64)
    merchantCategory: Optional[str] = Field(None, max_length=64)
    channel: Optional[str] = Field(None, max_length=32)
    location: Optional[GeoPoint] = None
    metadata: Optional[TransactionMetadata | Dict[str, Any]] = None


class FeatureSummary(BaseModel):
    amount: float
    device_risk: float
    geo_distance: float
    velocity: float


class ScoreResponse(BaseModel):
    score: float = Field(..., ge=0.0, le=1.0)
    rationale: str
    modelVersion: str
    featureContributions: FeatureSummary


__all__ = [
    "GeoPoint",
    "TransactionMetadata",
    "TransactionEvent",
    "FeatureSummary",
    "ScoreResponse",
]
