package com.othala.backend.model

import java.util.UUID

data class Skill(
    val id: UUID,
    val skill: String,
    val parentSkill: UUID? = null,
)
