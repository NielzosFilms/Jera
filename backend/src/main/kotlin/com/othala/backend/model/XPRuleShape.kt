package com.othala.backend.model

import kotlin.reflect.KClass

enum class XPRuleShape(val knobsClass: KClass<out XPRuleShapeKnobs>) {
    TIME_RATE(XPRuleShapeKnobs.TimeRate::class),
    FIXED(XPRuleShapeKnobs.Fixed::class);

    companion object {
        fun of(knobsClass: XPRuleShapeKnobs): XPRuleShape =
            entries.first { it.knobsClass == knobsClass::class }
    }
}
