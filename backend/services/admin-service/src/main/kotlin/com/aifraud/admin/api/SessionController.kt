package com.aifraud.admin.api

import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.model.StartSessionRequest
import com.aifraud.admin.service.SessionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/session")
class SessionController(
    private val sessionService: SessionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun startSession(@Valid @RequestBody request: StartSessionRequest): SessionResponse {
        return sessionService.createSession(request)
    }
}
