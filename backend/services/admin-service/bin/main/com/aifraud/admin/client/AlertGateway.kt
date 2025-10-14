package com.aifraud.admin.client

import com.aifraud.admin.model.AlertStatus
import com.aifraud.admin.model.AlertView
import com.aifraud.admin.model.PagedResponse
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.model.UpdateAlertStatusRequest
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Component
class AlertGateway(
    private val restTemplate: RestTemplate
) {

    private val logger = LoggerFactory.getLogger(AlertGateway::class.java)

    private val pageTypeRef = object : ParameterizedTypeReference<PagedResponse<AlertView>>() {}

    fun fetchAlerts(status: AlertStatus?, page: Int?, size: Int?, session: SessionResponse): PagedResponse<AlertView> {
        val uriBuilder = UriComponentsBuilder.fromUriString("/api/v1/alerts")
        status?.let { uriBuilder.queryParam("status", it.name) }
        page?.let { uriBuilder.queryParam("page", it) }
        size?.let { uriBuilder.queryParam("size", it) }

        logger.debug("Requesting alerts with status={} page={} size={}", status, page, size)

        val response = restTemplate.exchange(
            uriBuilder.toUriString(),
            HttpMethod.GET,
            HttpEntity<Unit>(authHeaders(session)),
            pageTypeRef
        )

        return requireNotNull(response.body) { "Alert gateway returned empty body" }
    }

    fun updateAlertStatus(alertId: UUID, status: AlertStatus, session: SessionResponse): AlertView {
        logger.debug("Updating alert {} to status {}", alertId, status)

        val headers = authHeaders(session).apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(UpdateAlertStatusRequest(status), headers)

        val response = restTemplate.exchange(
            "/api/v1/alerts/$alertId/status",
            HttpMethod.PATCH,
            entity,
            AlertView::class.java
        )

        return requireNotNull(response.body) { "Alert gateway returned empty body" }
    }

    private fun authHeaders(session: SessionResponse): HttpHeaders {
        return HttpHeaders().apply {
            setBearerAuth(session.token)
            accept = listOf(MediaType.APPLICATION_JSON)
        }
    }
}
