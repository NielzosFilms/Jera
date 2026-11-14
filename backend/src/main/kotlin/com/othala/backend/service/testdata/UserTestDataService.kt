package com.othala.backend.service.testdata

import com.othala.backend.repository.SkillProgressRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Profile("dev")
class UserTestDataService(
    private val skillProgressRepository: SkillProgressRepository,
    private val skillTestDataService: SkillTestDataService,
) {
    fun initUser(userId: UUID) {
        skillTestDataService.ids.values.forEach { skillId ->
            skillProgressRepository.init(skillId, userId)
        }
    }
}
