package com.aifraud.admin.model

import jakarta.validation.constraints.NotBlank

data class StartSessionRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)
