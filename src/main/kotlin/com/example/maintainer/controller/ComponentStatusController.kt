package com.example.maintainer.controller

import com.example.maintainer.api.ComponentStatusResponse
import com.example.maintainer.service.ComponentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/status")
class ComponentStatusController(
    private val componentService: ComponentService,
) {
    @GetMapping("/{componentName}")
    fun getComponentStatus(
        @PathVariable componentName: String,
    ): ResponseEntity<ComponentStatusResponse> {
        val response = componentService.getComponentStatus(componentName)
        return ResponseEntity.ok(response)
    }
}
