package com.aifraud.fraud.client

import com.aifraud.fraud.config.RiskScoringConfiguration
import com.aifraud.fraud.config.RiskScoringProperties
import com.aifraud.fraud.messaging.TransactionEvent
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime

class HttpRiskScoringClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: HttpRiskScoringClient

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString().removeSuffix("/")
        val properties = RiskScoringProperties(enabled = true, baseUrl = baseUrl, timeout = Duration.ofSeconds(2))
        val webClient = RiskScoringConfiguration().riskScoringWebClient(properties)
        client = HttpRiskScoringClient(webClient, properties)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `delegates scoring to remote service`() {
        val body = """
            {
              "score": 0.87,
              "rationale": "High velocity",
              "modelVersion": "heuristic-v1"
            }
        """.trimIndent()
        server.enqueue(MockResponse().setBody(body).setHeader("Content-Type", "application/json"))

        val score = client.score(sampleEvent())

        assertThat(score.value).isEqualTo(0.87)
        assertThat(score.rationale).contains("High velocity")
    }

    @Test
    fun `falls back to default heuristics on failure`() {
        server.enqueue(MockResponse().setResponseCode(500))

        val score = client.score(sampleEvent(amount = BigDecimal("50.00")))

        assertThat(score.value).isLessThan(0.5)
    }

    private fun sampleEvent(amount: BigDecimal = BigDecimal("1200.00")): TransactionEvent = TransactionEvent(
        transactionId = "txn-1",
        accountId = "acct-1",
        amount = amount,
        currency = "USD",
        merchantCategory = "electronics",
        channel = "online",
        deviceId = "device-9",
        metadata = mapOf("velocity_last_hour" to 2),
        createdAt = OffsetDateTime.now()
    )
}
