package com.aifraud.admin.api

import com.aifraud.admin.model.AlertStatus
import com.aifraud.admin.api.support.CurrentSession
import com.aifraud.admin.model.AlertView
import com.aifraud.admin.model.PagedResponse
import com.aifraud.admin.model.UpdateAlertStatusRequest
import com.aifraud.admin.service.AdminAlertService
import com.aifraud.admin.model.SessionResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/admin/alerts")
class AdminAlertController(
    private val adminAlertService: AdminAlertService
) {

    @GetMapping
    fun getAlerts(
        @RequestParam(required = false) status: AlertStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @CurrentSession session: SessionResponse
    ): PagedResponse<AlertView> {
        return adminAlertService.listAlerts(status, page, size, session)
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    fun updateStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAlertStatusRequest,
        @CurrentSession session: SessionResponse
    ): AlertView {
        return adminAlertService.updateAlertStatus(id, request.status, session)
    }
}
