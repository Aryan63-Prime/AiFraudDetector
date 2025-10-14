package com.aifraud.alert.model

import jakarta.validation.constraints.NotNull

data class UpdateAlertStatusRequest(
    @field:NotNull
    val status: AlertStatus
)
