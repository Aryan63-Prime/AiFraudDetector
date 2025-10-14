package com.aifraud.transaction.service

import com.aifraud.transaction.model.TransactionDetailResponse
import com.aifraud.transaction.persistence.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@Service
class TransactionQueryService(
    private val repository: TransactionRepository
) {

    private val logger = LoggerFactory.getLogger(TransactionQueryService::class.java)

    @Transactional(readOnly = true)
    fun findById(transactionId: String): TransactionDetailResponse {
        val tx = repository.findById(transactionId).orElseThrow {
            logger.warn("Transaction {} not found", transactionId)
            ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction $transactionId not found")
        }
        return TransactionDetailResponse.from(tx)
    }

    @Transactional(readOnly = true)
    fun listTransactions(accountId: String?, pageable: Pageable): Page<TransactionDetailResponse> {
        val page = if (accountId.isNullOrBlank()) {
            repository.findAll(pageable)
        } else {
            repository.findByAccountId(accountId, pageable)
        }
        return page.map(TransactionDetailResponse.Companion::from)
    }
}
