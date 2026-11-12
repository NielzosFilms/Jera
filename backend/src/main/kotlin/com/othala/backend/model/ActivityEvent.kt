package com.othala.backend.model

import java.time.OffsetDateTime
import java.util.UUID

data class ActivityEvent(
    val id: UUID,
    val userId: UUID,
    val occurredAt: OffsetDateTime,
    val payload: ActivityEventPayload,
    val source: ActivitySource,
    val status: ActivityStatus,
)
