package com.othala.backend.model

sealed interface ActivityEventPayload {
    data class ClimbingSession(val timeInMinutes: Int) : ActivityEventPayload
    data class ClimbingSend(val grade: String) : ActivityEventPayload
    data class GamingSession(val timeInMinutes: Int) : ActivityEventPayload
}
