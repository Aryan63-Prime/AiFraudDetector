package com.aifraud.alert.messaging

import com.aifraud.alert.service.AlertService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class FraudDecisionListenerTest {

    @Mock
    private lateinit var alertService: AlertService

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should delegate parsed event to alert service`() {
        val listener = FraudDecisionListener(objectMapper, alertService)
        val payload = objectMapper.writeValueAsString(
            FraudDecisionEvent(
                transactionId = "txn-123",
                riskScore = 0.81,
                riskLevel = "MEDIUM",
                recommendation = "REVIEW",
                evaluatedAt = OffsetDateTime.now()
            )
        )

        listener.handle(payload)

        val captor = ArgumentCaptor.forClass(FraudDecisionEvent::class.java)
        verify(alertService).recordDecision(captor.capture())
    }

    @Test
    fun `should swallow malformed payload`() {
        val listener = FraudDecisionListener(objectMapper, alertService)

        assertThatCode {
            listener.handle("not-json")
        }.doesNotThrowAnyException()
    }
}
