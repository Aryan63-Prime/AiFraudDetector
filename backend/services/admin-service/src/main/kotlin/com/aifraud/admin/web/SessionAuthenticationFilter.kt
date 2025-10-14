package com.aifraud.admin.web

import com.aifraud.admin.service.SessionService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionAuthenticationFilter(
    private val sessionService: SessionService
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/actuator") ||
            path == "/api/v1/admin/session" ||
            request.method == "OPTIONS"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorization?.startsWith("Bearer ") == true) {
            val token = authorization.substringAfter("Bearer ")
            val session = sessionService.resolve(token)
            if (session != null) {
                request.setAttribute(REQUEST_ATTRIBUTE_SESSION, session)
                filterChain.doFilter(request, response)
                return
            }
        }

        response.status = HttpStatus.UNAUTHORIZED.value()
    }

    companion object {
        const val REQUEST_ATTRIBUTE_SESSION = "adminSession"
    }
}
