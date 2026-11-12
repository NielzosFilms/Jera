package com.othala.backend.service.testdata

import com.othala.backend.model.AttributeEnum
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class AttributeTestDataService(
    private val jdbc: NamedParameterJdbcTemplate,
) {
    fun insert() {
        AttributeEnum.entries.forEach(::insert)
    }

    private fun insert(attribute: AttributeEnum): Int =
        jdbc.update(
            """
                INSERT INTO attributes (attribute) VALUES (:attribute)
            """.trimIndent(),
            mapOf(
                "attribute" to attribute.name,
            )
        )
}
