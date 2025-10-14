package com.aifraud.transaction.model

import com.aifraud.transaction.persistence.Transaction
import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransactionDetailResponse(
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
        fun from(entity: Transaction): TransactionDetailResponse = TransactionDetailResponse(
            transactionId = entity.id,
            accountId = entity.accountId,
            amount = entity.amount,
            currency = entity.currency,
            merchantCategory = entity.merchantCategory,
            channel = entity.channel,
            deviceId = entity.deviceId,
            metadata = entity.metadata,
            createdAt = entity.createdAt
        )
    }
}
