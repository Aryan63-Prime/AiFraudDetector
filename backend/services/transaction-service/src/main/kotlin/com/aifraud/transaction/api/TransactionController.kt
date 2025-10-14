package com.aifraud.transaction.api

import com.aifraud.transaction.model.TransactionDetailResponse
import com.aifraud.transaction.model.TransactionRequest
import com.aifraud.transaction.model.TransactionResponse
import com.aifraud.transaction.service.TransactionIntakeService
import com.aifraud.transaction.service.TransactionQueryService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
class TransactionController(
    private val transactionIntakeService: TransactionIntakeService,
    private val transactionQueryService: TransactionQueryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun submitTransaction(
        @Valid @RequestBody request: TransactionRequest
    ): TransactionResponse = transactionIntakeService.ingest(request)

    @GetMapping("/{transactionId}")
    fun getTransaction(@PathVariable transactionId: String): TransactionDetailResponse =
        transactionQueryService.findById(transactionId)

    @GetMapping
    fun listTransactions(
        @RequestParam(required = false) accountId: String?,
        @PageableDefault(size = 20) pageable: Pageable
    ): Page<TransactionDetailResponse> = transactionQueryService.listTransactions(accountId, pageable)
}
