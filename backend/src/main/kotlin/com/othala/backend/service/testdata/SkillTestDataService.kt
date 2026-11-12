package com.othala.backend.service.testdata

import com.othala.backend.model.Skill
import com.othala.backend.repository.SkillRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.UUID

data class SkillNode(val name: String, val children: List<SkillNode> = emptyList())

fun node(name: String, vararg children: SkillNode) = SkillNode(name, children.toList())

@Service
@Profile("dev")
class SkillTestDataService(
    private val skillRepository: SkillRepository,
) {
    private val _ids = mutableMapOf<String, UUID>()
    val ids: Map<String, UUID> get() = _ids

    fun id(name: String): UUID = _ids[name] ?: error("No test skill named [$name]")

    fun insert() {
        listOf(
            node(
                "Climbing",
                node("Bouldering"),
                node("Sport climbing"),
                node("Training"),
            ),
            node(
                "Engineering",
                node("Projects"),
                node("Learning"),
            ),
            node("Gaming"),
            node(
                "Music",
                node("Guitar"),
            ),
            node(
                "Mechanic",
                node("Car"),
                node("Motorcycle"),
            ),
            node(
                "Social",
                node("Events"),
                node("Friends"),
            ),
            node(
                "Vitality",
                node("Sleep"),
            ),
            node(
                "Mind",
                node("Reflection"),
            )
        ).forEach { insert(it, parent = null) }
    }

    private fun insert(node: SkillNode, parent: UUID?) {
        val id = UUID.randomUUID()
        skillRepository.insert(
            Skill(
                id,
                node.name,
                parent
            )
        )
        check(_ids.put(node.name, id) == null) { "Duplicate skill name [${node.name}]" }
        node.children.forEach { insert(it, parent = id) }
    }
}
