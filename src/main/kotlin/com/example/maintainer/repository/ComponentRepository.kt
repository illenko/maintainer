package com.example.maintainer.repository

import com.example.maintainer.domain.Component
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    @Query(
        """
        SELECT * FROM components c
        WHERE (:#{#filter.search} IS NULL OR c.name ILIKE CONCAT('%', :#{#filter.search}, '%'))
        ORDER BY c.name
        """,
    )
    fun findByFilter(
        filter: com.example.maintainer.api.ComponentFilter,
        pageable: Pageable,
    ): Page<Component>
}
