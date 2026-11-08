package com.othala.backend

import com.othala.backend.model.ActivityEvent
import com.othala.backend.model.ActivitySource
import com.othala.backend.model.ActivityStatus
import com.othala.backend.model.ActivityType
import com.othala.backend.repository.ActivityRepository
import com.othala.backend.service.ActivityService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.OffsetDateTime
import java.util.*


class FlowTest(
    @Autowired private val activityRepository: ActivityRepository,
) : BaseIntegrationTest() {

    @Test
    fun savesAnActivity() {
        val id = UUID.randomUUID()
        activityRepository.insert(ActivityEvent(
            id,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            ActivityType.CLIMB_SESSION,
            emptyList<String>(),
            ActivitySource.MANUAL,
            ActivityStatus.PENDING,
        ))


        val saved = activityRepository.findAll()
        assertThat(saved.size).isEqualTo(1)
        assertThat(saved[0].id).isEqualTo(id)
    }
}
