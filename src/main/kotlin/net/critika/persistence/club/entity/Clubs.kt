package net.critika.persistence.club.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.club.model.RuleSet
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Clubs : KotlinxUUIDTable("clubs") {
    val name = varchar("name", 255)
    val creator = reference("creator_id", Users)
    val members = reference("members_id", Users).nullable()
    val country = varchar("country", 255)
    val city = varchar("city", 255)
    val address = varchar("address", 255)
    val logo = varchar("logo", 255)
    val description = varchar("description", 255)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    val ruleSet = enumerationByName("rule_set", 255, RuleSet::class)
}