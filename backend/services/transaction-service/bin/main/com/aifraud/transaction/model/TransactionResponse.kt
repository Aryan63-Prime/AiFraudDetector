package com.aifraud.transaction.model

data class TransactionResponse(
    val transactionId: String,
    val status: String,
    val message: String
)
