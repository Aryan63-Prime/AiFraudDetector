package com.aifraud.admin.service

import com.aifraud.admin.config.AdminAuthProperties
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.model.SessionUser
import com.aifraud.admin.model.StartSessionRequest
import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    private val authProperties: AdminAuthProperties
) {
    private val sessions = ConcurrentHashMap<String, SessionResponse>()
    private val usersByUsername = ConcurrentHashMap<String, AdminAuthProperties.User>()

    @PostConstruct
    fun indexUsers() {
        usersByUsername.clear()
        authProperties.users.forEach { user ->
            usersByUsername[user.username.lowercase()] = user
        }
    }

    fun createSession(request: StartSessionRequest): SessionResponse {
        val usernameKey = request.username.trim().lowercase()
        val stored = usersByUsername[usernameKey]
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

        if (stored.password != request.password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        val token = UUID.randomUUID().toString()
        val expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plus(authProperties.sessionTtl)
        val response = SessionResponse(
            token = token,
            user = SessionUser(username = stored.username, roles = stored.roles),
            expiresAt = expiresAt
        )

        sessions[token] = response
        return response
    }

    fun resolve(token: String): SessionResponse? {
        val session = sessions[token] ?: return null
        return if (session.expiresAt.isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            sessions.remove(token)
            null
        } else {
            session
        }
    }
}
