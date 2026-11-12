package com.othala.backend.model

import kotlin.reflect.KClass

enum class ActivityType(val payloadClass: KClass<out ActivityEventPayload>) {
    CLIMB_SESSION(ActivityEventPayload.ClimbingSession::class),
    CLIMB_SEND(ActivityEventPayload.ClimbingSend::class),
    GAMING_SESSION(ActivityEventPayload.GamingSession::class);

    companion object {
        fun of(payload: ActivityEventPayload): ActivityType =
            entries.first { it.payloadClass == payload::class }
    }
}
