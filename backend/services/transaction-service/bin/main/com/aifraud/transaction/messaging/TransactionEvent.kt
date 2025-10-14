package com.aifraud.transaction.messaging

import com.aifraud.transaction.persistence.Transaction
import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransactionEvent(
    val transactionId: String,
    val accountId: String,
    val amount: BigDecimal,
    val currency: String,
    val merchantCategory: String,
    val channel: String?,
    val deviceId: String?,
    val metadata: Map<String, Any?>,
    val createdAt: OffsetDateTime
) {
    companion object {
        fun from(transaction: Transaction): TransactionEvent = TransactionEvent(
            transactionId = transaction.id,
            accountId = transaction.accountId,
            amount = transaction.amount,
            currency = transaction.currency,
            merchantCategory = transaction.merchantCategory,
            channel = transaction.channel,
            deviceId = transaction.deviceId,
            metadata = transaction.metadata,
            createdAt = transaction.createdAt
        )
    }
}
