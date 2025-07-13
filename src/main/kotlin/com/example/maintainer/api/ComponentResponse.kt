package com.example.maintainer.api

import java.time.LocalDateTime
import java.util.UUID

data class ComponentResponse(
    val id: UUID,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)