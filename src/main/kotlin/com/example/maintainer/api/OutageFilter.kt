package com.example.maintainer.api

import com.example.maintainer.domain.OutageType
import java.time.LocalDateTime
import java.util.UUID

data class OutageFilter(
    val type: OutageType? = null,
    val componentId: UUID? = null,
    val ongoing: Boolean? = null,
    val fromDate: LocalDateTime? = null,
    val toDate: LocalDateTime? = null,
) {
    fun hasFilters(): Boolean = type != null || componentId != null || ongoing != null || fromDate != null || toDate != null
}
