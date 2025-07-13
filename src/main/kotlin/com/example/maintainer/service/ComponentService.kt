package com.example.maintainer.service

import com.example.maintainer.api.ComponentRequest
import com.example.maintainer.api.ComponentResponse
import com.example.maintainer.api.ComponentStatusResponse
import com.example.maintainer.domain.ComponentStatus
import com.example.maintainer.exception.ComponentAlreadyExistsException
import com.example.maintainer.exception.ComponentNotFoundException
import com.example.maintainer.mapper.toEntity
import com.example.maintainer.mapper.toResponse
import com.example.maintainer.mapper.updateFrom
import com.example.maintainer.repository.ComponentRepository
import com.example.maintainer.repository.OutageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class ComponentService(
    private val componentRepository: ComponentRepository,
    private val outageRepository: OutageRepository,
) {
    fun createComponent(request: ComponentRequest): ComponentResponse {
        if (componentRepository.existsByName(request.name)) {
            throw ComponentAlreadyExistsException(request.name)
        }

        val component = request.toEntity()
        val savedComponent = componentRepository.save(component)
        return savedComponent.toResponse()
    }

    @Transactional(readOnly = true)
    fun getComponentById(id: UUID): ComponentResponse {
        val component =
            componentRepository
                .findById(id)
                .orElseThrow { ComponentNotFoundException(id.toString()) }
        return component.toResponse()
    }

    @Transactional(readOnly = true)
    fun getComponentByName(name: String): ComponentResponse {
        val component =
            componentRepository.findByName(name)
                ?: throw ComponentNotFoundException(name)
        return component.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllComponents(pageable: Pageable): Page<ComponentResponse> =
        componentRepository
            .findAll(pageable)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun searchComponents(namePattern: String): List<ComponentResponse> {
        val pattern = "%${namePattern.lowercase()}%"
        return componentRepository
            .findByNameContainingIgnoreCase(pattern)
            .map { it.toResponse() }
    }

    fun updateComponent(
        id: UUID,
        request: ComponentRequest,
    ): ComponentResponse {
        val existingComponent =
            componentRepository
                .findById(id)
                .orElseThrow { ComponentNotFoundException(id.toString()) }

        if (request.name != existingComponent.name && componentRepository.existsByName(request.name)) {
            throw ComponentAlreadyExistsException(request.name)
        }

        val updatedComponent = existingComponent.updateFrom(request)
        val savedComponent = componentRepository.save(updatedComponent)
        return savedComponent.toResponse()
    }

    fun deleteComponent(id: UUID) {
        if (!componentRepository.existsById(id)) {
            throw ComponentNotFoundException(id.toString())
        }
        componentRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getComponentStatus(componentName: String): ComponentStatusResponse {
        val component =
            componentRepository.findByName(componentName)
                ?: throw ComponentNotFoundException(componentName)

        val currentTime = LocalDateTime.now()
        val activeOutages = outageRepository.findActiveOutagesByComponentId(component.id!!, currentTime)

        val status =
            if (activeOutages.isEmpty()) {
                ComponentStatus.AVAILABLE
            } else {
                ComponentStatus.UNAVAILABLE
            }

        return ComponentStatusResponse(
            componentName = componentName,
            status = status,
            checkedAt = currentTime,
            activeOutagesCount = activeOutages.size,
        )
    }
}
