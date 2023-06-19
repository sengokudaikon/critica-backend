package net.critika.persistence.club.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Tournaments : KotlinxUUIDTable(name = "tournaments") {
    val name = varchar("name", 255)
    val clubId = reference("club_id", Clubs).nullable()
    val creator = reference("creator", Users)
    val date = datetime("date")
    val playerLimit = integer("player_limit")
    val location = varchar("location", 255)
    val description = text("description")
    val isFinished = bool("is_finished")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}
