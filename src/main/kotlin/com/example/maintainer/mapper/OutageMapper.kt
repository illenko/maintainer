package com.example.maintainer.mapper

import com.example.maintainer.api.OutageRequest
import com.example.maintainer.api.OutageResponse
import com.example.maintainer.domain.Outage
import java.time.LocalDateTime

fun OutageRequest.toEntity(): Outage =
    Outage(
        componentId = this.componentId,
        type = this.type,
        fromTime = this.fromTime,
        toTime = this.toTime,
        reason = this.reason,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

fun Outage.toResponse(componentName: String): OutageResponse =
    OutageResponse(
        id = this.id!!,
        componentId = this.componentId,
        componentName = componentName,
        type = this.type,
        fromTime = this.fromTime,
        toTime = this.toTime,
        reason = this.reason,
        isOngoing = this.toTime == null,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun Outage.updateFrom(request: OutageRequest): Outage =
    this.copy(
        componentId = request.componentId,
        type = request.type,
        fromTime = request.fromTime,
        toTime = request.toTime,
        reason = request.reason,
        updatedAt = LocalDateTime.now(),
    )

fun Outage.resolve(): Outage =
    this.copy(
        toTime = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )
