package com.othala.backend.model

import java.util.UUID

data class SkillAttribute(
    val skillId: UUID,
    val attribute: AttributeEnum,
    val weight: Double,
)
