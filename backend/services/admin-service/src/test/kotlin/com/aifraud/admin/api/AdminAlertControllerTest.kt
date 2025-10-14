package com.aifraud.admin.api

import com.aifraud.admin.model.AlertStatus
import com.aifraud.admin.model.AlertView
import com.aifraud.admin.model.PageableResponse
import com.aifraud.admin.model.PagedResponse
import com.aifraud.admin.model.Recommendation
import com.aifraud.admin.model.RiskLevel
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.model.SessionUser
import com.aifraud.admin.model.SortResponse
import com.aifraud.admin.model.UpdateAlertStatusRequest
import com.aifraud.admin.service.AdminAlertService
import com.aifraud.admin.web.SessionAuthenticationFilter
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import java.time.OffsetDateTime
import java.util.UUID

@WebMvcTest(AdminAlertController::class)
class AdminAlertControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var adminAlertService: AdminAlertService

    private val alertId = UUID.fromString("22222222-2222-2222-2222-222222222222")
    private val now = OffsetDateTime.parse("2024-01-01T11:00:00+00:00")
    private val session = SessionResponse(
        token = "test-token",
        user = SessionUser(username = "analyst", roles = listOf("ANALYST")),
        expiresAt = now.plusHours(1)
    )

    @Test
    fun `should return paged alerts`() {
        val response = pagedResponse()
        given(adminAlertService.listAlerts(status = null, page = 0, size = 20, session = session)).willReturn(response)

        mockMvc.get("/api/v1/admin/alerts") {
            requestAttr(SessionAuthenticationFilter.REQUEST_ATTRIBUTE_SESSION, session as Any)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.content[0].id", equalTo(alertId.toString()))
                jsonPath("$.content[0].riskLevel", equalTo("HIGH"))
            }
        verify(adminAlertService).listAlerts(null, 0, 20, session)
    }

    @Test
    fun `should update alert status`() {
        val updated = responseAlert(AlertStatus.DISMISSED)
        given(adminAlertService.updateAlertStatus(alertId, AlertStatus.DISMISSED, session)).willReturn(updated)

        mockMvc.patch("/api/v1/admin/alerts/{id}/status", alertId) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(UpdateAlertStatusRequest(AlertStatus.DISMISSED))
            requestAttr(SessionAuthenticationFilter.REQUEST_ATTRIBUTE_SESSION, session as Any)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status", equalTo("DISMISSED"))
        }
        verify(adminAlertService).updateAlertStatus(alertId, AlertStatus.DISMISSED, session)
    }

    private fun pagedResponse(): PagedResponse<AlertView> = PagedResponse(
        content = listOf(responseAlert(AlertStatus.ACKNOWLEDGED)),
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

    private fun responseAlert(status: AlertStatus): AlertView = AlertView(
        id = alertId,
        transactionId = "txn-456",
        riskScore = 0.75,
        riskLevel = RiskLevel.HIGH,
        recommendation = Recommendation.REVIEW,
        evaluatedAt = now,
        status = status,
        createdAt = now.minusDays(2),
        updatedAt = now
    )
}
