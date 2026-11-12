package com.othala.backend

import com.othala.backend.model.ActivityEvent
import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.ActivitySource
import com.othala.backend.model.ActivityStatus
import com.othala.backend.model.ActivityType
import com.othala.backend.model.XPRule
import com.othala.backend.repository.ActivityRepository
import com.othala.backend.repository.XPRuleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.*

@TestMethodOrder(MethodOrderer.DisplayName::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlowTest(
    @Autowired private val activityRepository: ActivityRepository,
    @Autowired private val xpRuleRepository: XPRuleRepository,
) : BaseIntegrationTest() {

    private val userId = UUID.randomUUID()

    private val climbingSessionId = UUID.randomUUID()
    private val climbingSendId = UUID.randomUUID()
    private val gamingSessionId = UUID.randomUUID()

    @Test
    fun `1 - Can insert ActivityEvents`() {
        activityRepository.insert(ActivityEvent(
            climbingSessionId,
            userId,
            OffsetDateTime.now(),
            ActivityEventPayload.ClimbingSession(120),
            ActivitySource.MANUAL,
            ActivityStatus.PENDING,
        ))

        activityRepository.insert(ActivityEvent(
            climbingSendId,
            userId,
            OffsetDateTime.now(),
            ActivityEventPayload.ClimbingSend("6c+"),
            ActivitySource.MANUAL,
            ActivityStatus.PENDING,
        ))

        activityRepository.insert(ActivityEvent(
            gamingSessionId,
            userId,
            OffsetDateTime.now(),
            ActivityEventPayload.GamingSession(180),
            ActivitySource.MANUAL,
            ActivityStatus.PENDING,
        ))

        val saved = activityRepository.findAll()
        assertThat(saved.size).isEqualTo(3)
        assertThat(saved[0].id).isEqualTo(climbingSessionId)
        assertThat(ActivityType.of(saved[0].payload)).isEqualTo(ActivityType.CLIMB_SESSION)
        assertThat(saved[1].id).isEqualTo(climbingSendId)
        assertThat(ActivityType.of(saved[1].payload)).isEqualTo(ActivityType.CLIMB_SEND)
        assertThat(saved[2].id).isEqualTo(gamingSessionId)
        assertThat(ActivityType.of(saved[2].payload)).isEqualTo(ActivityType.GAMING_SESSION)
    }

    @Test
    fun `2 - XPRules exist`() {
        val rules = xpRuleRepository.findAll()

        assertThat(rules.size).isEqualTo(3)
    }

    fun `2 - Can calculate XP using ActivityEvents`() {

    }
}
