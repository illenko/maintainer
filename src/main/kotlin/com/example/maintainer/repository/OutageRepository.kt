package com.example.maintainer.repository

import com.example.maintainer.domain.Outage
import com.example.maintainer.domain.OutageType
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
    fun findByComponentId(componentId: UUID): List<Outage>

    fun findByType(type: OutageType): List<Outage>

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

    @Query("SELECT * FROM outages WHERE to_time IS NULL")
    fun findOngoingOutages(): List<Outage>

    @Query(
        """
        SELECT * FROM outages 
        WHERE from_time >= :fromDate 
        AND from_time <= :toDate
        ORDER BY from_time DESC
        """,
    )
    fun findByDateRange(
        fromDate: LocalDateTime,
        toDate: LocalDateTime,
    ): List<Outage>

    @Query(
        """
        SELECT * FROM outages 
        WHERE component_id = :componentId 
        AND type = :type
        ORDER BY from_time DESC
        """,
    )
    fun findByComponentIdAndType(
        componentId: UUID,
        type: OutageType,
    ): List<Outage>
}
