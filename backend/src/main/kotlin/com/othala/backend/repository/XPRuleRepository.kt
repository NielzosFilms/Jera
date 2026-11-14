package com.othala.backend.repository

import com.othala.backend.model.ActivityType
import com.othala.backend.model.XPRule
import com.othala.backend.model.XPRuleShape
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Repository
class XPRuleRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
) {
    private val xpRuleMapper = RowMapper { rs, _ ->
        val shape = XPRuleShape.valueOf(rs.getString("shape"))
        XPRule(
            ActivityType.valueOf(rs.getString("activity_type")),
            UUID.fromString(rs.getString("skill_id")),
            objectMapper.readValue(rs.getString("shape_knobs"), shape.knobsClass.java)
        )
    }

    fun findAll(): List<XPRule> =
        jdbc.query("SELECT * FROM xp_rules", xpRuleMapper)

    fun findAllFor(activityType: ActivityType): List<XPRule> =
        jdbc.query(
            "SELECT * FROM xp_rules WHERE activity_type = :activityType",
            mapOf("activityType" to activityType.name),
            xpRuleMapper
        )

    // INSERT

    fun insert(xpRule: XPRule): Int =
        jdbc.update(
            """
                INSERT INTO xp_rules (activity_type, skill_id, shape, shape_knobs)
                VALUES (:activityType, :skillId, :shape, :shapeKnobs::jsonb)
            """.trimIndent(),
            mapOf(
                "activityType" to xpRule.activityType.name,
                "skillId" to xpRule.skillId,
                "shape" to XPRuleShape.of(xpRule.shapeKnobs).name,
                "shapeKnobs" to objectMapper.writeValueAsString(xpRule.shapeKnobs),
            )
        )
}
