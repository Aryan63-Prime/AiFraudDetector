package com.aifraud.admin.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("admin.auth")
class AdminAuthProperties {
    var sessionTtl: Duration = Duration.ofHours(8)
    var users: List<User> = listOf(User())

    class User {
        var username: String = "admin"
        var password: String = "changeme"
        var roles: List<String> = listOf("ANALYST")
    }
}
