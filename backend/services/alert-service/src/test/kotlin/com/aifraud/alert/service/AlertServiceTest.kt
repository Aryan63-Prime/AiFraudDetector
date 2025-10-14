package com.aifraud.alert.service

import com.aifraud.alert.messaging.FraudDecisionEvent
import com.aifraud.alert.model.AlertStatus
import com.aifraud.alert.model.Recommendation
import com.aifraud.alert.model.RiskLevel
import com.aifraud.alert.persistence.Alert
import com.aifraud.alert.persistence.AlertRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AlertServiceTest {

    @Mock
    private lateinit var alertRepository: AlertRepository

    private lateinit var alertService: AlertService

    @BeforeEach
    fun setUp() {
        alertService = AlertService(alertRepository)
    }

    @Test
    fun `recordDecision should create new alert when none exists`() {
        val event = FraudDecisionEvent(
            transactionId = "txn-1",
            riskScore = 0.92,
            riskLevel = "HIGH",
            recommendation = "BLOCK",
            evaluatedAt = OffsetDateTime.now()
        )

        `when`(alertRepository.findByTransactionId(event.transactionId)).thenReturn(null)

        alertService.recordDecision(event)

        val captor = ArgumentCaptor.forClass(Alert::class.java)
        verify(alertRepository).save(captor.capture())
        val alert = captor.value
        assertThat(alert.transactionId).isEqualTo(event.transactionId)
        assertThat(alert.riskLevel).isEqualTo(RiskLevel.HIGH)
        assertThat(alert.recommendation).isEqualTo(Recommendation.BLOCK)
        assertThat(alert.status).isEqualTo(AlertStatus.OPEN)
    }

    @Test
    fun `updateStatus should modify alert when status changes`() {
        val alertId = UUID.randomUUID()
        val existing = Alert(
            id = alertId,
            transactionId = "txn-2",
            riskScore = 0.4,
            riskLevel = RiskLevel.MEDIUM,
            recommendation = Recommendation.REVIEW,
            evaluatedAt = OffsetDateTime.now(),
            status = AlertStatus.OPEN
        )
        `when`(alertRepository.findById(alertId)).thenReturn(Optional.of(existing))

        val updated = alertService.updateStatus(alertId, AlertStatus.ACKNOWLEDGED)

        assertThat(updated.status).isEqualTo(AlertStatus.ACKNOWLEDGED)
        verify(alertRepository).save(existing)
    }
}
