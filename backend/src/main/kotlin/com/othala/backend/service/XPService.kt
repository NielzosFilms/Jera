package com.othala.backend.service

import com.othala.backend.model.ActivityEvent
import com.othala.backend.model.ActivityType
import com.othala.backend.repository.SkillProgressRepository
import com.othala.backend.repository.XPRuleRepository
import com.othala.backend.service.testdata.TestDataService.Companion.userId
import org.springframework.stereotype.Service

@Service
class XPService(
    private val xpRuleRepository: XPRuleRepository,
    private val xpRuleService: XPRuleService,
    private val skillProgressRepository: SkillProgressRepository,
) {
    fun award(event: ActivityEvent): List<Int> =
        xpRuleRepository.findAllFor(ActivityType.of(event.payload)).map { rule ->
            val skill = skillProgressRepository.get(rule.skillId, userId)
                ?: error("Could not find skill with id [${rule.skillId}] for user [${userId}]")

            skillProgressRepository.update(
                rule.skillId,
                userId,
                skill.xp + xpRuleService.calculate(event.payload, rule.shapeKnobs),
                skill.level,
            )
        }
}
