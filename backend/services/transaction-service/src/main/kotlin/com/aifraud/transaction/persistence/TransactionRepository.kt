package com.aifraud.transaction.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, String> {
	fun findByAccountId(accountId: String, pageable: Pageable): Page<Transaction>
}
