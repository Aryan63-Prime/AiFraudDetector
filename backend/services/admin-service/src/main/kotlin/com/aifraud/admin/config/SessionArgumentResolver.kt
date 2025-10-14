package com.aifraud.admin.config

import com.aifraud.admin.api.support.CurrentSession
import com.aifraud.admin.model.SessionResponse
import com.aifraud.admin.web.SessionAuthenticationFilter
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class SessionArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentSession::class.java) &&
            parameter.parameterType == SessionResponse::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        return webRequest.getAttribute(SessionAuthenticationFilter.REQUEST_ATTRIBUTE_SESSION, 0)
    }
}
