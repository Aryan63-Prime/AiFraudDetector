package com.aifraud.fraud.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "fraud.scoring.http")
data class RiskScoringProperties(
    val enabled: Boolean = false,
    val baseUrl: String = "http://localhost:9090",
    val timeout: Duration = Duration.ofSeconds(2)
)
