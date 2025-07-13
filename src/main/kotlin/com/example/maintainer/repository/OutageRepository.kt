package com.example.maintainer.repository

import com.example.maintainer.domain.Outage
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface OutageRepository : CrudRepository<Outage, UUID> {
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
}
