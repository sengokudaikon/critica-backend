package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashedPassword", 255)
    val playerName = varchar("playerName", 255)
    val isAdmin = bool("isAdmin").default(false)
}