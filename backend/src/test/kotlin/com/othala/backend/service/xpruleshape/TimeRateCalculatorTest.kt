package com.othala.backend.service.xpruleshape

import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.XPRuleShapeKnobs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TimeRateCalculatorTest {
    private val calculator = TimeRateCalculator()

    @Test
    fun `Just rate should work correctly`() {
        assertThat(calculator.calculate(payload(50), knobs(8.0))).isEqualTo(400)
        assertThat(calculator.calculate(payload(23), knobs(1.4))).isEqualTo(32)
    }

    @Test
    fun `BaseXP should be added`() {
        assertThat(calculator.calculate(payload(30), knobs(4.0, 20.0))).isEqualTo(140)
    }

    @Test
    fun `XPRate should stop after cap`() {
        assertThat(calculator.calculate(payload(61), knobs(1.0, cappedAfterMinutes = 60))).isEqualTo(60)
        assertThat(calculator.calculate(payload(120), knobs(1.0, cappedAfterMinutes = 60))).isEqualTo(60)
    }

    @Test
    fun `XPRate should stop after cap with baseXP`() {
        assertThat(calculator.calculate(payload(61), knobs(1.0, 20.0, cappedAfterMinutes = 60))).isEqualTo(80)
        assertThat(calculator.calculate(payload(120), knobs(1.0, 20.0, cappedAfterMinutes = 60))).isEqualTo(80)
    }

    @Test
    fun `XPRate with higher cap should not subtract XP`() {
        assertThat(calculator.calculate(payload(20), knobs(1.0, cappedAfterMinutes = 60, rateMultiplierAfterCap = 1.0))).isEqualTo(20)
    }

    @Test
    fun `XPRate multiplier should be applied only after cap`() {
        assertThat(calculator.calculate(payload(120), knobs(1.0, cappedAfterMinutes = 60, rateMultiplierAfterCap = 0.5))).isEqualTo(90)
    }

    @Test
    fun `Negative time should throw`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(payload(-1), knobs(1.0))
        }
    }

    @Test
    fun `All parameters set should return correct XP`() {
        val knobs = knobs(1.0, 10.0, 60, 0.5)

        assertThat(calculator.calculate(payload(0), knobs)).isEqualTo(10)
        assertThat(calculator.calculate(payload(30), knobs)).isEqualTo(40)
        assertThat(calculator.calculate(payload(60), knobs)).isEqualTo(70)
        assertThat(calculator.calculate(payload(90), knobs)).isEqualTo(85)
    }

    private fun payload(timeInMinutes: Int) = ActivityEventPayload.ClimbingSession(timeInMinutes)

    private fun knobs(
        rateXP: Double,
        baseXP: Double? = null,
        cappedAfterMinutes: Int? = null,
        rateMultiplierAfterCap: Double? = null,
    ) = XPRuleShapeKnobs.TimeRate(
        rateXP,
        baseXP,
        cappedAfterMinutes,
        rateMultiplierAfterCap,
    )
}
