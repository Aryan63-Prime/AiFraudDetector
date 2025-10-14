package com.aifraud.alert.model

import com.aifraud.alert.persistence.Alert
import java.time.OffsetDateTime
import java.util.UUID

data class AlertResponse(
    val id: UUID,
    val transactionId: String,
    val riskScore: Double,
    val riskLevel: RiskLevel,
    val recommendation: Recommendation,
    val evaluatedAt: OffsetDateTime,
    val status: AlertStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(alert: Alert): AlertResponse = AlertResponse(
            id = alert.id!!,
            transactionId = alert.transactionId,
            riskScore = alert.riskScore,
            riskLevel = alert.riskLevel,
            recommendation = alert.recommendation,
            evaluatedAt = alert.evaluatedAt,
            status = alert.status,
            createdAt = alert.createdAt,
            updatedAt = alert.updatedAt
        )
    }
}
