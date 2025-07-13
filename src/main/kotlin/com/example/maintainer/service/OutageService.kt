package com.example.maintainer.service

import com.example.maintainer.api.OutageRequest
import com.example.maintainer.api.OutageResponse
import com.example.maintainer.exception.ComponentNotFoundException
import com.example.maintainer.exception.InvalidOutageException
import com.example.maintainer.exception.OutageAlreadyResolvedException
import com.example.maintainer.exception.OutageNotFoundException
import com.example.maintainer.mapper.resolve
import com.example.maintainer.mapper.toEntity
import com.example.maintainer.mapper.toResponse
import com.example.maintainer.mapper.updateFrom
import com.example.maintainer.repository.ComponentRepository
import com.example.maintainer.repository.OutageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class OutageService(
    private val outageRepository: OutageRepository,
    private val componentRepository: ComponentRepository,
) {
    fun createOutage(request: OutageRequest): OutageResponse {
        validateOutageRequest(request)

        val component =
            componentRepository
                .findById(request.componentId)
                .orElseThrow { ComponentNotFoundException(request.componentId.toString()) }

        val outage = request.toEntity()
        val savedOutage = outageRepository.save(outage)
        return savedOutage.toResponse(component.name)
    }

    @Transactional(readOnly = true)
    fun getOutageById(id: UUID): OutageResponse {
        val outage =
            outageRepository
                .findById(id)
                .orElseThrow { OutageNotFoundException(id) }

        val component =
            componentRepository
                .findById(outage.componentId)
                .orElseThrow { ComponentNotFoundException(outage.componentId.toString()) }

        return outage.toResponse(component.name)
    }

    @Transactional(readOnly = true)
    fun getAllOutages(): List<OutageResponse> =
        outageRepository.findAll().map { outage ->
            val component =
                componentRepository
                    .findById(outage.componentId)
                    .orElseThrow { ComponentNotFoundException(outage.componentId.toString()) }
            outage.toResponse(component.name)
        }

    fun updateOutage(
        id: UUID,
        request: OutageRequest,
    ): OutageResponse {
        val existingOutage =
            outageRepository
                .findById(id)
                .orElseThrow { OutageNotFoundException(id) }

        validateOutageRequest(request)

        val component =
            componentRepository
                .findById(request.componentId)
                .orElseThrow { ComponentNotFoundException(request.componentId.toString()) }

        val updatedOutage = existingOutage.updateFrom(request)
        val savedOutage = outageRepository.save(updatedOutage)
        return savedOutage.toResponse(component.name)
    }

    fun resolveOutage(id: UUID): OutageResponse {
        val existingOutage =
            outageRepository
                .findById(id)
                .orElseThrow { OutageNotFoundException(id) }

        if (existingOutage.toTime != null) {
            throw OutageAlreadyResolvedException(id)
        }

        val component =
            componentRepository
                .findById(existingOutage.componentId)
                .orElseThrow { ComponentNotFoundException(existingOutage.componentId.toString()) }

        val resolvedOutage = existingOutage.resolve()
        val savedOutage = outageRepository.save(resolvedOutage)
        return savedOutage.toResponse(component.name)
    }

    fun deleteOutage(id: UUID) {
        if (!outageRepository.existsById(id)) {
            throw OutageNotFoundException(id)
        }
        outageRepository.deleteById(id)
    }

    private fun validateOutageRequest(request: OutageRequest) {
        if (request.fromTime.isBefore(LocalDateTime.now())) {
            throw InvalidOutageException("Outage start time cannot be in the past")
        }

        if (request.toTime != null && request.toTime.isBefore(request.fromTime)) {
            throw InvalidOutageException("Outage end time cannot be before start time")
        }
    }
}
