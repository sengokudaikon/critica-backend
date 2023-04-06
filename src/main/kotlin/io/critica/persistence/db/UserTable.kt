package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val playerName = varchar("player_name", 255)
    val isAdmin = bool("is_admin").default(false)
}