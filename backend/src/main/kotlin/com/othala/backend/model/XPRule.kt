package com.othala.backend.model

import java.util.UUID

data class XPRule(
    val activityType: ActivityType,
    val skillId: UUID,
    val shapeKnobs: XPRuleShapeKnobs,
)
