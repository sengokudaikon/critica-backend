package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.user.model.UserRole
import net.critika.persistence.club.entity.Clubs
import org.jetbrains.exposed.sql.javatime.datetime

object Users : KotlinxUUIDTable(name = "users") {
    val uid = varchar("uid", 255).uniqueIndex()
    val clubId = reference("club_id", Clubs)
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val playerName = varchar("player_name", 255)
    val username = varchar("username", 255).uniqueIndex()
    val role = enumeration("role", UserRole::class)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
