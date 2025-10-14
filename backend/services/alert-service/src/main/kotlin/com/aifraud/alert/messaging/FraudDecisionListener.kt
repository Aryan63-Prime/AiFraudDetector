package com.aifraud.alert.messaging

import com.aifraud.alert.service.AlertService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FraudDecisionListener(
    private val objectMapper: ObjectMapper,
    private val alertService: AlertService
) {

    private val logger = LoggerFactory.getLogger(FraudDecisionListener::class.java)

    @KafkaListener(topics = ["\${kafka.topics.fraud-decisions}"], groupId = "alert-service")
    fun handle(message: String) {
        try {
            val event: FraudDecisionEvent = objectMapper.readValue(message)
            logger.debug("Received fraud decision for transaction {}", event.transactionId)
            alertService.recordDecision(event)
        } catch (ex: Exception) {
            logger.error("Failed to process fraud decision payload", ex)
            // TODO: send to dead-letter topic or raise alert
        }
    }
}
