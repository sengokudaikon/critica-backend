package io.critica.persistence.db

import io.critica.domain.GameStatus
import org.jetbrains.exposed.dao.id.IntIdTable

object Games : IntIdTable() {
    val lobbyId = reference("lobby_id", Lobbies)
    val date = varchar("date", 255)
    val status = enumeration("status", GameStatus::class)
    val winner = varchar("winner", 255).nullable()
}
