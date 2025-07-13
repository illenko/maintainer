package com.example.maintainer.repository

import com.example.maintainer.domain.Component
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ComponentRepository : CrudRepository<Component, UUID> {
    fun findByName(name: String): Component?

    fun existsByName(name: String): Boolean
}
