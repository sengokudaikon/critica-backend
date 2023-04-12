package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object PlayerTable : IntIdTable() {
    val userId = reference("userId", UserTable)
    val gameId = reference("gameId", GameTable)
    val name = varchar("name", 255)
    val status = varchar("status", 255)
    val role = varchar("role", 255)
    val bonusPoints = integer("bonus_points").default(0)
}