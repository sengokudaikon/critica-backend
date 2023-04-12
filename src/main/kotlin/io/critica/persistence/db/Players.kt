package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object Players : IntIdTable() {
    val userId = reference("userId", Users).nullable()
    val lobbyId = reference("lobbyId", Lobbies)
    val gameId = reference("gameId", Games).nullable()
    val name = varchar("name", 255)
    val status = varchar("status", 255)
    val role = varchar("role", 255)
    val bonusPoints = integer("bonus_points").default(0)
}