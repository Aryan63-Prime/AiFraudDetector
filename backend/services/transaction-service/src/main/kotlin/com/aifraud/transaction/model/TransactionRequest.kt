package com.aifraud.transaction.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class TransactionRequest(
    @field:NotBlank
    val transactionId: String,
    @field:NotBlank
    val accountId: String,
    @field:NotNull
    @field:DecimalMin(value = "0.0", inclusive = false)
    val amount: BigDecimal,
    @field:NotBlank
    val currency: String,
    @field:NotBlank
    val merchantCategory: String,
    val channel: String? = null,
    val deviceId: String? = null,
    val metadata: Map<String, Any?> = emptyMap()
)
