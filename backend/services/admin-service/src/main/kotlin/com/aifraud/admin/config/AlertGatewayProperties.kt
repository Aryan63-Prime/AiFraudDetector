package com.aifraud.admin.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "alerts.gateway")
data class AlertGatewayProperties(
    val baseUrl: String = "http://localhost:8083",
    val timeout: Duration = Duration.ofSeconds(5)
)
