package com.example.maintainer.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ComponentRequest(
    @field:NotBlank(message = "Component name is required")
    @field:Size(min = 2, max = 100, message = "Component name must be between 2 and 100 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9][a-zA-Z0-9-_]*[a-zA-Z0-9]$",
        message = "Component name must be URL-safe (alphanumeric, hyphens, underscores only)"
    )
    val name: String,
)