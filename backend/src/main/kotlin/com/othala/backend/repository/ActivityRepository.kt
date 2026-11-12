package com.othala.backend.repository

import com.othala.backend.model.ActivityEvent
import com.othala.backend.model.ActivitySource
import com.othala.backend.model.ActivityStatus
import com.othala.backend.model.ActivityType
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class ActivityRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
) {
    private val activityEventMapper = RowMapper { rs, _ ->
        val type = ActivityType.valueOf(rs.getString("type"))
        ActivityEvent(
            rs.getObject("id", UUID::class.java),
            rs.getObject("user_id", UUID::class.java),
            rs.getObject("occurred_at", OffsetDateTime::class.java),
            objectMapper.readValue(rs.getString("payload"), type.payloadClass.java),
            ActivitySource.valueOf(rs.getString("source")),
            ActivityStatus.valueOf(rs.getString("status")),
        )
    }

    fun findAll(): List<ActivityEvent> =
        jdbc.query("SELECT * FROM activity_events", activityEventMapper)

    fun insert(activityEvent: ActivityEvent): Int =
        jdbc.update(
            """
                INSERT INTO activity_events (id, user_id, occurred_at, type, payload, source, status)
                VALUES (:id, :userId, :occurredAt, :type, :payload::jsonb, :source, :status)
                """.trimIndent(),
            mapOf(
                "id" to activityEvent.id,
                "userId" to activityEvent.userId,
                "occurredAt" to activityEvent.occurredAt,
                "type" to ActivityType.of(activityEvent.payload).name,
                "payload" to objectMapper.writeValueAsString(activityEvent.payload),
                "source" to activityEvent.source.name,
                "status" to activityEvent.status.name
            ),
        )
}
