package com.example.maintainer.api

import com.example.maintainer.domain.ComponentStatus
import java.time.LocalDateTime

data class ComponentStatusResponse(
    val componentName: String,
    val status: ComponentStatus,
    val checkedAt: LocalDateTime,
    val activeOutagesCount: Int = 0,
)
