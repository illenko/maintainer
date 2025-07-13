package com.example.maintainer.repository

import com.example.maintainer.domain.Component
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ComponentRepository :
    CrudRepository<Component, UUID>,
    PagingAndSortingRepository<Component, UUID> {
    fun findByName(name: String): Component?

    fun existsByName(name: String): Boolean

    @Query("SELECT * FROM components WHERE name ILIKE :namePattern")
    fun findByNameContainingIgnoreCase(namePattern: String): List<Component>
}
