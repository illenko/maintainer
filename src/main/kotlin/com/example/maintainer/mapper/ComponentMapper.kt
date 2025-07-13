package com.example.maintainer.mapper

import com.example.maintainer.api.ComponentRequest
import com.example.maintainer.api.ComponentResponse
import com.example.maintainer.domain.Component
import java.time.LocalDateTime

fun ComponentRequest.toEntity(): Component =
    Component(
        name = this.name,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

fun Component.toResponse(): ComponentResponse =
    ComponentResponse(
        id = this.id!!,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun Component.updateFrom(request: ComponentRequest): Component =
    this.copy(
        name = request.name,
        updatedAt = LocalDateTime.now(),
    )