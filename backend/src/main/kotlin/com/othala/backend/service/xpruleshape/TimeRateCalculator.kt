package com.othala.backend.service.xpruleshape

import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.Timed
import com.othala.backend.model.XPRuleShapeKnobs
import org.springframework.stereotype.Component
import kotlin.math.roundToInt

@Component
class TimeRateCalculator : TypedXPRuleShapeCalculator<XPRuleShapeKnobs.TimeRate>(
    XPRuleShapeKnobs.TimeRate::class,
) {
    override fun calculateTyped(payload: ActivityEventPayload, knobs: XPRuleShapeKnobs.TimeRate): Int {
        val minutes = (payload as? Timed)?.timeInMinutes
            ?: throw IllegalStateException(
                "TIME_RATE rule can't be applied to ${payload::class.simpleName}"
            )
        require(minutes >= 0) { "Negative duration: $minutes" }

        val cap = knobs.cappedAfterMinutes
        val regularMinutes = if (cap != null) minutes.coerceAtMost(cap) else minutes
        val extraMinutes = if (cap != null) (minutes - cap).coerceAtLeast(0) else 0

        val base = knobs.baseXP ?: 0.0
        val regularXP = knobs.rateXP * regularMinutes
        val extraXP = knobs.rateXP * extraMinutes * (knobs.rateMultiplierAfterCap ?: 0.0)

        return (base + regularXP + extraXP).roundToInt()
    }
}
