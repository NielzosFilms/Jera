package com.othala.backend

import com.othala.backend.model.ActivityEvent
import com.othala.backend.model.ActivityEventPayload
import com.othala.backend.model.ActivitySource
import com.othala.backend.model.ActivityStatus
import com.othala.backend.repository.ActivityRepository
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
) : BaseIntegrationTest() {

    @Test
    fun `a asdf`() {
        val id = UUID.randomUUID()
        activityRepository.insert(ActivityEvent(
            id,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            ActivityEventPayload.ClimbingSession(120),
            ActivitySource.MANUAL,
            ActivityStatus.PENDING,
        ))


        val saved = activityRepository.findAll()
        assertThat(saved.size).isEqualTo(1)
        assertThat(saved[0].id).isEqualTo(id)
    }
}
