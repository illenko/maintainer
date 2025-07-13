package com.example.maintainer.api

import com.example.maintainer.domain.OutageType
import java.time.LocalDateTime
import java.util.UUID

data class OutageResponse(
    val id: UUID,
    val componentId: UUID,
    val componentName: String,
    val type: OutageType,
    val fromTime: LocalDateTime,
    val toTime: LocalDateTime?,
    val reason: String?,
    val isOngoing: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
