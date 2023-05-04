package net.critika.persistence.db

import net.critika.domain.GameStatus
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Games : UUIDTable("games") {
    val hostId = reference("host_id", Users).nullable()
    val lobbyId = reference("lobby_id", Lobbies)
    val date = datetime("date")
    val status = enumeration("status", GameStatus::class)
    val winner = varchar("winner", 255).nullable()
}
