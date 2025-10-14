package com.aifraud.fraud.messaging

import com.aifraud.fraud.service.FraudEvaluationService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class FraudEventListenerTest {

    @Mock
    private lateinit var evaluationService: FraudEvaluationService

    @Test
    fun `should delegate to evaluation service`() {
        val objectMapper = jacksonObjectMapper()
        val listener = FraudEventListener(objectMapper, evaluationService)

        val payload = objectMapper.writeValueAsString(
            TransactionEvent(
                transactionId = "txn-abc",
                accountId = "acc-xyz",
                amount = BigDecimal("1200"),
                currency = "USD",
                merchantCategory = "ELECTRONICS",
                channel = "WEB",
                deviceId = "device-1",
                metadata = mapOf("ip" to "127.0.0.1"),
                createdAt = OffsetDateTime.now()
            )
        )

        listener.onTransactionEvent(payload)

        val captor = ArgumentCaptor.forClass(TransactionEvent::class.java)
        verify(evaluationService).evaluate(captor.capture())
        val event = captor.value
        assertThat(event.transactionId).isEqualTo("txn-abc")
        assertThat(event.amount).isEqualTo(BigDecimal("1200"))
    }
}
