package com.aifraud.transaction.messaging

import com.aifraud.transaction.persistence.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.stereotype.Component

@Component
class TransactionEventSerializer(
    objectMapper: ObjectMapper
) {

    private val mapper = objectMapper.copy().registerKotlinModule()

    fun serialize(transaction: Transaction): String {
        val event = TransactionEvent.from(transaction)
        return mapper.writeValueAsString(event)
    }
}
