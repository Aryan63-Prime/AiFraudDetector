package com.aifraud.transaction.service

import com.aifraud.transaction.messaging.TransactionEventSerializer
import com.aifraud.transaction.model.TransactionRequest
import com.aifraud.transaction.model.TransactionResponse
import com.aifraud.transaction.persistence.Transaction
import com.aifraud.transaction.persistence.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class TransactionIntakeService(
    private val repository: TransactionRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val transactionEventSerializer: TransactionEventSerializer,
    @Value("\${kafka.topics.transactions-raw}")
    private val transactionsTopic: String,
) {

    private val logger = LoggerFactory.getLogger(TransactionIntakeService::class.java)

    @Transactional
    fun ingest(request: TransactionRequest): TransactionResponse {
        logger.info("Received transaction {} for account {}", request.transactionId, request.accountId)
        val entity = Transaction(
            id = request.transactionId,
            accountId = request.accountId,
            amount = request.amount,
            currency = request.currency,
            merchantCategory = request.merchantCategory,
            channel = request.channel,
            deviceId = request.deviceId,
            metadata = request.metadata,
            createdAt = OffsetDateTime.now()
        )

        repository.save(entity)
        val payload = transactionEventSerializer.serialize(entity)
        kafkaTemplate.send(transactionsTopic, entity.id, payload)
        logger.debug("Published transaction {} to Kafka topic {}", entity.id, transactionsTopic)
        return TransactionResponse(
            transactionId = request.transactionId,
            status = "ACCEPTED",
            message = "Transaction queued for fraud analysis"
        )
    }
}
