package com.othala.backend.model

interface Timed {
    val timeInMinutes: Int
}

sealed interface ActivityEventPayload {
    data class ClimbingSession(override val timeInMinutes: Int) : ActivityEventPayload, Timed
    data class ClimbingSend(val grade: String) : ActivityEventPayload
    data class GamingSession(override val timeInMinutes: Int) : ActivityEventPayload, Timed
}
