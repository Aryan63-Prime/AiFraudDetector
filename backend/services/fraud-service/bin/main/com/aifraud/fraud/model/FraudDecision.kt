package com.aifraud.fraud.model

import java.time.OffsetDateTime

data class FraudDecision(
    val transactionId: String,
    val riskScore: Double,
    val riskLevel: RiskLevel,
    val recommendation: Recommendation,
    val evaluatedAt: OffsetDateTime
)

enum class RiskLevel { LOW, MEDIUM, HIGH }

enum class Recommendation { ALLOW, REVIEW, BLOCK }
