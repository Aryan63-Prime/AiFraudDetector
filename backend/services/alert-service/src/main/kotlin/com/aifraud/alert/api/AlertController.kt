package com.aifraud.alert.api

import com.aifraud.alert.model.AlertResponse
import com.aifraud.alert.model.AlertStatus
import com.aifraud.alert.model.UpdateAlertStatusRequest
import com.aifraud.alert.service.AlertService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
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
@RequestMapping("/api/v1/alerts")
class AlertController(
    private val alertService: AlertService
) {

    @GetMapping
    fun getAlerts(
        @RequestParam(required = false) status: AlertStatus?,
        @PageableDefault(size = 20) pageable: Pageable
    ): Page<AlertResponse> {
        val result = alertService.listAlerts(status, pageable)
        return result.map(AlertResponse.Companion::from)
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    fun updateAlertStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAlertStatusRequest
    ): AlertResponse {
        val updated = alertService.updateStatus(id, request.status)
        return AlertResponse.from(updated)
    }
}
