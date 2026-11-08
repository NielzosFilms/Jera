package com.othala.backend.model

import com.othala.backend.model.xpruleshape.XPRuleShape
import java.util.UUID

data class XPRule(
    val activityType: ActivityType,
    val skillId: UUID,
    val shape: XPRuleShape,
    val shapeKnobs: Any,
)
