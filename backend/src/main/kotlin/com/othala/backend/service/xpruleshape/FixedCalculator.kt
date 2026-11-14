package com.othala.backend.service.xpruleshape

import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.XPRuleShapeKnobs
import org.springframework.stereotype.Component
import kotlin.math.roundToInt

@Component
class FixedCalculator : TypedXPRuleShapeCalculator<XPRuleShapeKnobs.Fixed>(
    XPRuleShapeKnobs.Fixed::class,
) {
    override fun calculateTyped(payload: ActivityEventPayload, knobs: XPRuleShapeKnobs.Fixed): Int =
        knobs.xp.roundToInt()
}
