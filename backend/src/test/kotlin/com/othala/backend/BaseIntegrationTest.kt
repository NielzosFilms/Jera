package com.othala.backend

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.postgresql.PostgreSQLContainer

@SpringBootTest
@ImportTestcontainers
open class BaseIntegrationTest {
    companion object {
        @ServiceConnection
        @JvmStatic
        var postgres: PostgreSQLContainer = PostgreSQLContainer("postgres:17")
    }
}
