package net.critika.persistence.db

import net.critika.domain.user.model.UserRole
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Users : UUIDTable(name = "users") {
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val playerName = varchar("player_name", 255)
    val role = enumeration("role", UserRole::class)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
