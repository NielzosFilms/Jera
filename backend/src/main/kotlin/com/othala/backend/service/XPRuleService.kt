package com.othala.backend.service

import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.XPRuleShapeKnobs
import com.othala.backend.service.xpruleshape.XPRuleShapeCalculator
import org.springframework.stereotype.Service

@Service
class XPRuleService(calculators: List<XPRuleShapeCalculator>) {
    private val byKnobs = calculators.associateBy { it.knobsClass }

    fun calculate(payload: ActivityEventPayload, knobs: XPRuleShapeKnobs): Long =
        (byKnobs[knobs::class] ?: error("No calculator for ${knobs::class.simpleName}"))
            .calculate(payload, knobs)
}
