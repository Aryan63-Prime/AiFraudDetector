package com.aifraud.admin.model

import java.time.OffsetDateTime
import java.util.UUID

data class AlertView(
    val id: UUID,
    val transactionId: String,
    val riskScore: Double,
    val riskLevel: RiskLevel,
    val recommendation: Recommendation,
    val evaluatedAt: OffsetDateTime,
    val status: AlertStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
