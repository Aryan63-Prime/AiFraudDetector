package com.aifraud.alert.service

import com.aifraud.alert.messaging.FraudDecisionEvent
import com.aifraud.alert.model.AlertStatus
import com.aifraud.alert.model.Recommendation
import com.aifraud.alert.model.RiskLevel
import com.aifraud.alert.persistence.Alert
import com.aifraud.alert.persistence.AlertRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AlertService(
    private val alertRepository: AlertRepository
) {

    private val logger = LoggerFactory.getLogger(AlertService::class.java)

    @Transactional
    fun recordDecision(event: FraudDecisionEvent) {
        val riskLevel = parseRiskLevel(event.riskLevel)
        val recommendation = parseRecommendation(event.recommendation)
        val existing = alertRepository.findByTransactionId(event.transactionId)
        if (existing != null) {
            logger.info("Updating existing alert for transaction {}", event.transactionId)
            existing.riskScore = event.riskScore
            existing.riskLevel = riskLevel
            existing.recommendation = recommendation
            existing.evaluatedAt = event.evaluatedAt
            existing.updatedAt = OffsetDateTime.now()
            alertRepository.save(existing)
        } else {
            logger.info("Creating new alert for transaction {}", event.transactionId)
            val alert = Alert(
                transactionId = event.transactionId,
                riskScore = event.riskScore,
                riskLevel = riskLevel,
                recommendation = recommendation,
                evaluatedAt = event.evaluatedAt,
                status = AlertStatus.OPEN
            )
            alertRepository.save(alert)
        }
    }

    @Transactional(readOnly = true)
    fun listAlerts(status: AlertStatus?, pageable: Pageable): Page<Alert> {
        return if (status != null) {
            alertRepository.findByStatus(status, pageable)
        } else {
            alertRepository.findAll(pageable)
        }
    }

    @Transactional
    fun updateStatus(alertId: UUID, newStatus: AlertStatus): Alert {
        val alert = alertRepository.findById(alertId)
            .orElseThrow { IllegalArgumentException("Alert $alertId not found") }
        if (alert.status != newStatus) {
            alert.status = newStatus
            alert.updatedAt = OffsetDateTime.now()
            alertRepository.save(alert)
        }
        return alert
    }

    private fun parseRiskLevel(value: String): RiskLevel = runCatching {
        RiskLevel.valueOf(value.uppercase())
    }.getOrElse {
        logger.warn("Received unknown risk level '{}', defaulting to MEDIUM", value)
        RiskLevel.MEDIUM
    }

    private fun parseRecommendation(value: String): Recommendation = runCatching {
        Recommendation.valueOf(value.uppercase())
    }.getOrElse {
        logger.warn("Received unknown recommendation '{}', defaulting to REVIEW", value)
        Recommendation.REVIEW
    }
}
