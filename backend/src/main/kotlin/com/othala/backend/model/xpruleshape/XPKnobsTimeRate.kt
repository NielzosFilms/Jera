package com.othala.backend.model.xpruleshape

data class XPKnobsTimeRate(
    val baseXP: Double?,
    val rateXP: Double,
    val cappedAfterMinutes: Int?,
    val rateMultiplierAfterCap: Double?,
)
