package com.othala.backend.repository

import com.othala.backend.model.Skill
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Repository
class SkillRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
) {
    private val skillMapper = RowMapper { rs, _ ->
        Skill(
            UUID.fromString(rs.getString("id")),
            rs.getString("skill"),
            UUID.fromString(rs.getString("parent_skill"))
        )
    }

    fun insert(skill: Skill): Int =
        jdbc.update(
            """
                INSERT INTO skills (id, skill, parent_skill)
                VALUES (:id, :skill, :parentSkill)
            """.trimIndent(),
            mapOf(
                "id" to skill.id,
                "skill" to skill.skill,
                "parentSkill" to skill.parentSkill,
            )
        )
}
