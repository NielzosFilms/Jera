package com.othala.backend.service.testdata

import com.othala.backend.model.AttributeEnum
import com.othala.backend.model.Skill
import com.othala.backend.model.SkillAttribute
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
        // Skills
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

        // SkillAttributes
        // STR
        skillRepository.insert(SkillAttribute(
            id("Bouldering"),
            AttributeEnum.STR,
            0.6
        ))
        skillRepository.insert(SkillAttribute(
            id("Training"),
            AttributeEnum.STR,
            0.6
        ))
        // DEX
        skillRepository.insert(SkillAttribute(
            id("Climbing"),
            AttributeEnum.DEX,
            0.3
        ))
        skillRepository.insert(SkillAttribute(
            id("Guitar"),
            AttributeEnum.DEX,
            0.5
        ))
        skillRepository.insert(SkillAttribute(
            id("Gaming"),
            AttributeEnum.DEX,
            0.3
        ))
        // END
        skillRepository.insert(SkillAttribute(
            id("Sport climbing"),
            AttributeEnum.END,
            0.6
        ))
        skillRepository.insert(SkillAttribute(
            id("Sleep"),
            AttributeEnum.END,
            0.4
        ))
        // INT
        skillRepository.insert(SkillAttribute(
            id("Engineering"),
            AttributeEnum.INT,
            0.6
        ))
        skillRepository.insert(SkillAttribute(
            id("Mechanic"),
            AttributeEnum.INT,
            0.4
        ))
        // WIS
        skillRepository.insert(SkillAttribute(
            id("Mind"),
            AttributeEnum.WIS,
            0.7
        ))
        skillRepository.insert(SkillAttribute(
            id("Sleep"),
            AttributeEnum.WIS,
            0.3
        ))
        skillRepository.insert(SkillAttribute(
            id("Gaming"),
            AttributeEnum.WIS,
            0.2
        ))
        // CHA
        skillRepository.insert(SkillAttribute(
            id("Social"),
            AttributeEnum.CHA,
            1.0
        ))
        // CRE
        skillRepository.insert(SkillAttribute(
            id("Guitar"),
            AttributeEnum.CRE,
            0.4
        ))
        skillRepository.insert(SkillAttribute(
            id("Engineering"),
            AttributeEnum.CRE,
            0.3
        ))
        skillRepository.insert(SkillAttribute(
            id("Mechanic"),
            AttributeEnum.CRE,
            0.4
        ))
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
