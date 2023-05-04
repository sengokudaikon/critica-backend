package net.critika.persistence.db

import net.critika.domain.PlayerRole
import net.critika.domain.PlayerStatus
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.max
import java.util.*

object Players : IdTable<UUID>(name = "players") {
    override val id = uuid("id").entityId()
    val userId = reference("user_id", Users).nullable()
    val lobbyId = reference("lobby_id", Lobbies).nullable()
    val gameId = reference("game_id", Games).nullable()
    val name = varchar("name", 255)
    val status = varchar("status", 255).default(PlayerStatus.WAITING.name)
    val role = varchar("role", 255).nullable().default(PlayerRole.CITIZEN.name)
    val bestMove = integer("best_move").default(0)
    val bonusPoints = integer("bonus_points").default(0)
    val seat = integer("seat").default(0)
    val foulPoints = integer("foul_points").default(0)
}
