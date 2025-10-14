package com.aifraud.admin.web

import com.aifraud.admin.api.support.CurrentSession
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping

@Component
class SessionExtractingInterceptor(
    private val sessionService: SessionService
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorization?.startsWith("Bearer ") == true) {
            val token = authorization.substringAfter("Bearer ")
            val session = sessionService.resolve(token)
            if (session != null) {
                request.setAttribute(CurrentSession::class.qualifiedName, session)
                return true
            }
        }

        if (request.servletPath == "/api/v1/admin/session" || request.servletPath.startsWith("/actuator")) {
            return true
        }

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        return false
    }
}
