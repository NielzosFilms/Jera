package com.othala.backend.model

import java.util.UUID

data class SkillProgress(
    val skillId: UUID,
    val userId: UUID,
    val xp: Double,
    val level: Int,
)
