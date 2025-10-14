package com.aifraud.alert.persistence

import com.aifraud.alert.model.AlertStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlertRepository : JpaRepository<Alert, UUID> {
    fun findByStatus(status: AlertStatus, pageable: Pageable): Page<Alert>
    fun findByTransactionId(transactionId: String): Alert?
}
