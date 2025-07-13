package com.example.maintainer.repository

import com.example.maintainer.domain.Outage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface OutageRepository :
    CrudRepository<Outage, UUID>,
    PagingAndSortingRepository<Outage, UUID> {
    @Query(
        """
        SELECT * FROM outages 
        WHERE component_id = :componentId 
        AND from_time <= :currentTime 
        AND (to_time IS NULL OR to_time > :currentTime)
        """,
    )
    fun findActiveOutagesByComponentId(
        componentId: UUID,
        currentTime: LocalDateTime,
    ): List<Outage>

    @Query(
        """
        SELECT * FROM outages o
        WHERE (:#{#filter.type} IS NULL OR o.type = :#{#filter.type})
        AND (:#{#filter.componentId} IS NULL OR o.component_id = :#{#filter.componentId})
        AND (:#{#filter.ongoing} IS NULL OR 
             (:#{#filter.ongoing} = true AND o.to_time IS NULL) OR
             (:#{#filter.ongoing} = false AND o.to_time IS NOT NULL))
        AND (:#{#filter.fromDate} IS NULL OR o.from_time >= :#{#filter.fromDate})
        AND (:#{#filter.toDate} IS NULL OR o.from_time <= :#{#filter.toDate})
        ORDER BY o.from_time DESC
        """,
    )
    fun findByFilter(
        filter: com.example.maintainer.api.OutageFilter,
        pageable: Pageable,
    ): Page<Outage>
}
