package com.aifraud.admin.service

import com.aifraud.admin.client.AlertGateway
import com.aifraud.admin.model.AlertStatus
import com.aifraud.admin.model.AlertView
import com.aifraud.admin.model.PagedResponse
import com.aifraud.admin.model.SessionResponse
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AdminAlertService(
    private val alertGateway: AlertGateway
) {

    fun listAlerts(status: AlertStatus?, page: Int?, size: Int?, session: SessionResponse): PagedResponse<AlertView> {
        return alertGateway.fetchAlerts(status, page, size, session)
    }

    fun updateAlertStatus(alertId: UUID, newStatus: AlertStatus, session: SessionResponse): AlertView {
        return alertGateway.updateAlertStatus(alertId, newStatus, session)
    }
}
