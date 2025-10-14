package com.aifraud.fraud

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): Map<String, Any> = mapOf(
        "status" to "UP",
        "service" to "fraud-service",
        "timestamp" to Instant.now().toEpochMilli()
    )
}
