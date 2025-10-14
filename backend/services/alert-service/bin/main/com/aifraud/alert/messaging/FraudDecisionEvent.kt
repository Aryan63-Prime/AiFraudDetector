package com.aifraud.alert.messaging

import java.time.OffsetDateTime

data class FraudDecisionEvent(
    val transactionId: String,
    val riskScore: Double,
    val riskLevel: String,
    val recommendation: String,
    val evaluatedAt: OffsetDateTime
)
