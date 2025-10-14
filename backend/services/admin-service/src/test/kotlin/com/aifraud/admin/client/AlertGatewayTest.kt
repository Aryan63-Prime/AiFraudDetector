package com.aifraud.admin.client

import com.aifraud.admin.config.AlertGatewayProperties
import com.aifraud.admin.config.GatewayConfig
import com.aifraud.admin.model.AlertStatus
import com.aifraud.admin.model.AlertView
import com.aifraud.admin.model.PageableResponse
import com.aifraud.admin.model.PagedResponse
import com.aifraud.admin.model.Recommendation
import com.aifraud.admin.model.RiskLevel
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.model.SessionUser
import com.aifraud.admin.model.SortResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.time.OffsetDateTime
import java.util.UUID

@RestClientTest(AlertGateway::class)
@EnableConfigurationProperties(AlertGatewayProperties::class)
@Import(GatewayConfig::class)
@TestPropertySource(
    properties = [
        "alerts.gateway.base-url=http://localhost:9999",
        "alerts.gateway.timeout=3s"
    ]
)
class AlertGatewayTest @Autowired constructor(
    private val gateway: AlertGateway,
    private val server: MockRestServiceServer,
    private val objectMapper: ObjectMapper
) {

    private val alertId = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val now = OffsetDateTime.parse("2024-01-01T10:15:30+00:00")
    private val session = SessionResponse(
        token = "test-token",
        user = SessionUser(username = "analyst", roles = listOf("ANALYST")),
        expiresAt = now.plusHours(1)
    )

    @Test
    fun `fetch alerts delegates to alert service`() {
        val response = pagedResponse()

        server.expect(requestTo("http://localhost:9999/api/v1/alerts?page=0&size=20"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer ${session.token}"))
            .andRespond(
                withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON)
            )

        val result = gateway.fetchAlerts(status = null, page = 0, size = 20, session = session)

        assertThat(result.content).hasSize(1)
        assertThat(result.totalElements).isEqualTo(1)
        server.verify()
    }

    @Test
    fun `update alert status calls alert service`() {
        val alert = responseAlert()
        server.expect(requestTo("http://localhost:9999/api/v1/alerts/$alertId/status"))
            .andExpect(method(HttpMethod.PATCH))
            .andExpect(header("Authorization", "Bearer ${session.token}"))
            .andRespond(
                withSuccess(objectMapper.writeValueAsString(alert), MediaType.APPLICATION_JSON)
            )

        val result = gateway.updateAlertStatus(alertId, AlertStatus.ACKNOWLEDGED, session)

        assertThat(result.status).isEqualTo(AlertStatus.ACKNOWLEDGED)
        server.verify()
    }

    private fun pagedResponse(): PagedResponse<AlertView> = PagedResponse(
        content = listOf(responseAlert()),
        pageable = PageableResponse(
            pageNumber = 0,
            pageSize = 20,
            offset = 0,
            paged = true,
            unpaged = false,
            sort = SortResponse(empty = true, sorted = false, unsorted = true)
        ),
        totalElements = 1,
        totalPages = 1,
        last = true,
        size = 20,
        number = 0,
        sort = SortResponse(empty = true, sorted = false, unsorted = true),
        numberOfElements = 1,
        first = true,
        empty = false
    )

    private fun responseAlert(): AlertView = AlertView(
        id = alertId,
        transactionId = "txn-123",
        riskScore = 0.92,
        riskLevel = RiskLevel.HIGH,
        recommendation = Recommendation.BLOCK,
        evaluatedAt = now,
        status = AlertStatus.ACKNOWLEDGED,
        createdAt = now.minusDays(1),
        updatedAt = now
    )
}
