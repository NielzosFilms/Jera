package com.othala.backend.service.testdata

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Profile("dev")
class TestDataService(
    private val jdbc: JdbcTemplate,
    private val attributeTestDataService: AttributeTestDataService,
    private val skillTestDataService: SkillTestDataService,
    private val xpRuleTestDataService: XPRuleTestDataService,
    private val userTestDataService: UserTestDataService,
) {
    private val log = LoggerFactory.getLogger(TestDataService::class.java)

    companion object {
        val userId = UUID.randomUUID()
    }

    fun isDatabaseFilled(): Boolean =
        jdbc.queryForObject<Boolean>("SELECT EXISTS (SELECT 1 FROM attributes)") ?: false

    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        if (isDatabaseFilled()) {
            log.info("Database already filled, skipping test data")
            return
        }
        log.info("Database empty, inserting test data...")

        attributeTestDataService.insert()
        skillTestDataService.insert()
        xpRuleTestDataService.insert()

        userTestDataService.initUser(userId)

        log.info("Done inserting test data")
    }
}
