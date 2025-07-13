package com.example.maintainer.controller

import com.example.maintainer.api.OutageRequest
import com.example.maintainer.api.OutageResponse
import com.example.maintainer.service.OutageService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/outages")
class OutageController(
    private val outageService: OutageService,
) {
    @PostMapping
    fun createOutage(
        @Valid @RequestBody request: OutageRequest,
    ): ResponseEntity<OutageResponse> {
        val response = outageService.createOutage(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getOutageById(
        @PathVariable id: UUID,
    ): ResponseEntity<OutageResponse> {
        val response = outageService.getOutageById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllOutages(): ResponseEntity<List<OutageResponse>> {
        val response = outageService.getAllOutages()
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateOutage(
        @PathVariable id: UUID,
        @Valid @RequestBody request: OutageRequest,
    ): ResponseEntity<OutageResponse> {
        val response = outageService.updateOutage(id, request)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/resolve")
    fun resolveOutage(
        @PathVariable id: UUID,
    ): ResponseEntity<OutageResponse> {
        val response = outageService.resolveOutage(id)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteOutage(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        outageService.deleteOutage(id)
        return ResponseEntity.noContent().build()
    }
}
