package com.example.maintainer.api

import com.example.maintainer.domain.OutageType
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class OutageRequest(
    @field:NotNull(message = "Component ID is required")
    val componentId: UUID,
    @field:NotNull(message = "Outage type is required")
    val type: OutageType,
    @field:NotNull(message = "Start time is required")
    @field:Future(message = "Start time must be in the future")
    val fromTime: LocalDateTime,
    val toTime: LocalDateTime? = null,
    @field:Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    val reason: String? = null,
)
