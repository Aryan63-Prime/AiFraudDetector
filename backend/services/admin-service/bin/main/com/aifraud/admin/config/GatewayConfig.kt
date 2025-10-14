package com.aifraud.admin.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class GatewayConfig {

    @Bean
    fun alertRestTemplate(
        builder: RestTemplateBuilder,
        properties: AlertGatewayProperties
    ): RestTemplate {
        val sanitizedBaseUrl = properties.baseUrl.trimEnd('/')
        return builder
            .rootUri(sanitizedBaseUrl)
            .setConnectTimeout(properties.timeout)
            .setReadTimeout(properties.timeout)
            .build()
    }
}
