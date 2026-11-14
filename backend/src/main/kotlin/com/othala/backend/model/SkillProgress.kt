package com.othala.backend.model

import java.util.UUID

data class SkillProgress(
    val skillId: UUID,
    val userId: UUID,
    val xp: Long,
    val level: Int,
)
