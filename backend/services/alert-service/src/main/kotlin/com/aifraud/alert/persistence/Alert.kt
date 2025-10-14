package com.aifraud.alert.persistence

import com.aifraud.alert.model.AlertStatus
import com.aifraud.alert.model.Recommendation
import com.aifraud.alert.model.RiskLevel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "fraud_alerts")
data class Alert(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "transaction_id", nullable = false, unique = true, length = 64)
    val transactionId: String,

    @Column(name = "risk_score", nullable = false)
    var riskScore: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 16)
    var riskLevel: RiskLevel,

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false, length = 16)
    var recommendation: Recommendation,

    @Column(name = "evaluated_at", nullable = false)
    var evaluatedAt: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    var status: AlertStatus = AlertStatus.OPEN,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
