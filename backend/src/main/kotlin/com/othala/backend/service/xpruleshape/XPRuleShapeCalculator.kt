package com.othala.backend.service.xpruleshape

import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.XPRuleShapeKnobs
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface XPRuleShapeCalculator {
    val knobsClass: KClass<out XPRuleShapeKnobs>
    fun calculate(payload: ActivityEventPayload, knobs: XPRuleShapeKnobs): Long
}

abstract class TypedXPRuleShapeCalculator<K : XPRuleShapeKnobs>(
    override val knobsClass: KClass<K>,
) : XPRuleShapeCalculator {
    final override fun calculate(payload: ActivityEventPayload, knobs: XPRuleShapeKnobs): Long =
        calculateTyped(payload, knobsClass.cast(knobs))

    protected abstract fun calculateTyped(payload: ActivityEventPayload, knobs: K): Long
}
