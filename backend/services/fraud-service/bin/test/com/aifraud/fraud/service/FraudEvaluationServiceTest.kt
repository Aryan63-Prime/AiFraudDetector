package com.aifraud.fraud.service

import com.aifraud.fraud.client.RiskScoringClient
import com.aifraud.fraud.client.RiskScore
import com.aifraud.fraud.messaging.FraudDecisionPublisher
import com.aifraud.fraud.messaging.TransactionEvent
import com.aifraud.fraud.model.Recommendation
import com.aifraud.fraud.model.RiskLevel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import java.math.BigDecimal
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class FraudEvaluationServiceTest {

    @Mock
    private lateinit var riskScoringClient: RiskScoringClient

    @Mock
    private lateinit var decisionPublisher: FraudDecisionPublisher

    @Test
    fun `should publish high risk decision when score exceeds threshold`() {
        val event = TransactionEvent(
            transactionId = "txn-1",
            accountId = "acc-1",
            amount = BigDecimal("5000"),
            currency = "USD",
            merchantCategory = "ELECTRONICS",
            channel = "WEB",
            deviceId = null,
            metadata = emptyMap(),
            createdAt = OffsetDateTime.now()
        )

    `when`(riskScoringClient.score(event)).thenReturn(RiskScore(0.95, "High amount"))

        val service = FraudEvaluationService(riskScoringClient, decisionPublisher)
        service.evaluate(event)

        val decisionCaptor = ArgumentCaptor.forClass(com.aifraud.fraud.model.FraudDecision::class.java)
        verify(decisionPublisher).publish(decisionCaptor.capture())

        val decision = decisionCaptor.value
        assertThat(decision.riskLevel).isEqualTo(RiskLevel.HIGH)
        assertThat(decision.recommendation).isEqualTo(Recommendation.BLOCK)
    }
}
