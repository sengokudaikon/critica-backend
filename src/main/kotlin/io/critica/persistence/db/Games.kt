package io.critica.persistence.db

import io.critica.domain.GameStatus
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object Games : IdTable<UUID>() {
    override val id = uuid("id").entityId()
    val lobbyId = reference("lobby_id", Lobbies)
    val date = varchar("date", 255)
    val status = enumeration("status", GameStatus::class)
    val winner = varchar("winner", 255).nullable()
}
