package com.example.maintainer.api

data class ComponentFilter(
    val search: String? = null,
) {
    fun hasFilters(): Boolean = !search.isNullOrBlank()
}
