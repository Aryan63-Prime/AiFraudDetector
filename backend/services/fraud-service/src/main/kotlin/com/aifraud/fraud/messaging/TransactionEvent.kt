package com.aifraud.fraud.messaging

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
    val metadata: Map<String, Any?> = emptyMap(),
    val createdAt: OffsetDateTime
)
