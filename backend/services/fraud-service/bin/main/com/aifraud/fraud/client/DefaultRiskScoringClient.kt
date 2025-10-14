package com.aifraud.fraud.client

import com.aifraud.fraud.messaging.TransactionEvent
import org.slf4j.LoggerFactory
import kotlin.math.min

class DefaultRiskScoringClient : RiskScoringClient {

    private val logger = LoggerFactory.getLogger(DefaultRiskScoringClient::class.java)

    override fun score(event: TransactionEvent): RiskScore {
        val base = when {
            event.amount.toDouble() >= 5000 -> 0.95
            event.amount.toDouble() >= 1000 -> 0.75
            else -> 0.25
        }
        val devicePenalty = if (event.deviceId.isNullOrBlank()) 0.1 else 0.0
        val finalScore = min(1.0, base + devicePenalty)
        val rationale = buildString {
            append("Baseline risk score computed from amount tier.")
            if (devicePenalty > 0.0) {
                append(" Unknown device increased risk.")
            }
        }
        logger.debug("Calculated risk score {} for transaction {}", finalScore, event.transactionId)
        return RiskScore(value = finalScore, rationale = rationale)
    }
}
