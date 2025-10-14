package com.aifraud.fraud.service

import com.aifraud.fraud.client.RiskScoringClient
import com.aifraud.fraud.messaging.TransactionEvent
import com.aifraud.fraud.messaging.FraudDecisionPublisher
import com.aifraud.fraud.model.FraudDecision
import com.aifraud.fraud.model.Recommendation
import com.aifraud.fraud.model.RiskLevel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class FraudEvaluationService(
    private val riskScoringClient: RiskScoringClient,
    private val decisionPublisher: FraudDecisionPublisher
) {

    private val logger = LoggerFactory.getLogger(FraudEvaluationService::class.java)

    fun evaluate(event: TransactionEvent) {
        val score = riskScoringClient.score(event)
        val riskLevel = when {
            score.value >= 0.9 -> RiskLevel.HIGH
            score.value >= 0.5 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        val recommendation = when (riskLevel) {
            RiskLevel.HIGH -> Recommendation.BLOCK
            RiskLevel.MEDIUM -> Recommendation.REVIEW
            RiskLevel.LOW -> Recommendation.ALLOW
        }

        val decision = FraudDecision(
            transactionId = event.transactionId,
            riskScore = score.value,
            riskLevel = riskLevel,
            recommendation = recommendation,
            evaluatedAt = OffsetDateTime.now()
        )

        logger.info(
            "Fraud evaluation for transaction {} resulted in {} (score={})",
            event.transactionId,
            riskLevel,
            score.value
        )

        decisionPublisher.publish(decision)
    }
}
