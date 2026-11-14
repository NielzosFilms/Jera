package com.othala.backend.model

sealed interface XPRuleShapeKnobs {
    data class TimeRate(
        val rateXP: Double,
        val baseXP: Double? = null,
        val cappedAfterMinutes: Int? = null,
        val rateMultiplierAfterCap: Double? = null,
    ) : XPRuleShapeKnobs

    data class Fixed(
        val xp: Long,
    ) : XPRuleShapeKnobs
}
