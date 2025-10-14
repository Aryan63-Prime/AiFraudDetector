package com.aifraud.fraud.messaging

import com.aifraud.fraud.model.FraudDecision
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class FraudDecisionPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    objectMapper: ObjectMapper,
    @Value("\${kafka.topics.fraud-decisions}")
    private val decisionTopic: String
) {

    private val logger = LoggerFactory.getLogger(FraudDecisionPublisher::class.java)
    private val mapper = objectMapper.copy().registerKotlinModule()

    fun publish(decision: FraudDecision) {
        val payload = mapper.writeValueAsString(decision)
        kafkaTemplate.send(decisionTopic, decision.transactionId, payload)
        logger.info("Published fraud decision for transaction {} with risk {}", decision.transactionId, decision.riskLevel)
    }
}
