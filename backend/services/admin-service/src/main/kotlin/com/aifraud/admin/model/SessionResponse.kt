package com.aifraud.admin.model

import java.time.OffsetDateTime

data class SessionResponse(
    val token: String,
    val user: SessionUser,
    val expiresAt: OffsetDateTime
)
