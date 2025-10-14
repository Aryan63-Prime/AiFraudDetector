package com.aifraud.fraud.messaging

import com.aifraud.fraud.service.FraudEvaluationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FraudEventListener(
    private val objectMapper: ObjectMapper,
    private val evaluationService: FraudEvaluationService
) {

    private val logger = LoggerFactory.getLogger(FraudEventListener::class.java)

    @KafkaListener(
        topics = ["\${kafka.topics.transactions-raw}"],
        groupId = "fraud-service"
    )
    fun onTransactionEvent(payload: String) {
        try {
            val event: TransactionEvent = objectMapper.readValue(payload)
            logger.debug("Processing transaction event {}", event.transactionId)
            evaluationService.evaluate(event)
        } catch (ex: Exception) {
            logger.error("Failed to process transaction event payload", ex)
            // TODO: send to dead-letter topic or alerting pipeline
        }
    }
}
