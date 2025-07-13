package com.example.maintainer.controller

import com.example.maintainer.api.ComponentFilter
import com.example.maintainer.api.ComponentRequest
import com.example.maintainer.api.ComponentResponse
import com.example.maintainer.service.ComponentService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/components")
class ComponentController(
    private val componentService: ComponentService,
) {
    @PostMapping
    fun createComponent(
        @Valid @RequestBody request: ComponentRequest,
    ): ResponseEntity<ComponentResponse> {
        val response = componentService.createComponent(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getComponentById(
        @PathVariable id: UUID,
    ): ResponseEntity<ComponentResponse> {
        val response = componentService.getComponentById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/name/{name}")
    fun getComponentByName(
        @PathVariable name: String,
    ): ResponseEntity<ComponentResponse> {
        val response = componentService.getComponentByName(name)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getComponents(
        @PageableDefault(size = 20) pageable: Pageable,
        @RequestParam(required = false) search: String?,
    ): ResponseEntity<Page<ComponentResponse>> {
        val filter = ComponentFilter(search = search)
        val response = componentService.getComponents(filter, pageable)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateComponent(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ComponentRequest,
    ): ResponseEntity<ComponentResponse> {
        val response = componentService.updateComponent(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteComponent(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        componentService.deleteComponent(id)
        return ResponseEntity.noContent().build()
    }
}
