package com.othala.backend.repository

import com.othala.backend.model.SkillProgress
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Repository
class SkillProgressRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
) {
    private val mapper = RowMapper { rs, _ ->
        SkillProgress(
            UUID.fromString(rs.getString("skill_id")),
            UUID.fromString(rs.getString("user_id")),
            rs.getLong("xp"),
            rs.getInt("level"),
        )
    }

    fun init(skillId: UUID, userId: UUID) =
        jdbc.update(
            """
                INSERT INTO skill_progress(skill_id, user_id, xp, level)
                VALUES (:skillId, :userId, 0, 1)
            """.trimIndent(),
            mapOf("skillId" to skillId, "userId" to userId),
        )

    fun get(skillId: UUID, userId: UUID): SkillProgress? =
        jdbc.query(
            """
                SELECT * FROM skill_progress
                WHERE skill_id = :skillId AND user_id = :userId
            """.trimIndent(),
            mapOf("skillId" to skillId, "userId" to userId),
            mapper
        ).firstOrNull()

    fun update(skillId: UUID, userId: UUID, xp: Long, level: Int): Int =
        jdbc.update(
            """
                UPDATE skill_progress
                SET xp = :xp, level = :level
                WHERE skill_id = :skillId AND user_id = :userId
            """.trimIndent(),
            mapOf("xp" to xp, "level" to level, "skillId" to skillId, "userId" to userId),
        )
}
