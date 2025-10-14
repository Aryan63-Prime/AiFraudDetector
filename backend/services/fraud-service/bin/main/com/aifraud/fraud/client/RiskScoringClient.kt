package com.aifraud.fraud.client

import com.aifraud.fraud.messaging.TransactionEvent

interface RiskScoringClient {
    fun score(event: TransactionEvent): RiskScore
}

data class RiskScore(
    val value: Double,
    val rationale: String
)
