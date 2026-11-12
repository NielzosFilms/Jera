package com.othala.backend.service.testdata

import com.othala.backend.model.ActivityType
import com.othala.backend.model.XPRule
import com.othala.backend.model.XPRuleShapeKnobs
import com.othala.backend.repository.XPRuleRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class XPRuleTestDataService(
    private val xpRuleRepository: XPRuleRepository,
    private val skills: SkillTestDataService,
) {
    private fun ruleFor(type: ActivityType): XPRule = when (type) {
        ActivityType.CLIMB_SESSION -> XPRule(
            ActivityType.CLIMB_SESSION,
            skills.id("Climbing"),
            XPRuleShapeKnobs.TimeRate(1.0)
        )
        ActivityType.CLIMB_SEND -> XPRule(
            ActivityType.CLIMB_SEND,
            skills.id("Climbing"),
            XPRuleShapeKnobs.Fixed(120.0)
        )
        ActivityType.GAMING_SESSION -> XPRule(
            ActivityType.GAMING_SESSION,
            skills.id("Gaming"),
            XPRuleShapeKnobs.TimeRate(1.0)
        )
    }

    fun insert() {
        ActivityType.entries
            .map(::ruleFor)
            .forEach { xpRuleRepository.insert(it) }
    }
}
