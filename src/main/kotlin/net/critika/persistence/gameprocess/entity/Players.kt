package net.critika.persistence.gameprocess.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.gameprocess.model.PlayerStatus
import net.critika.persistence.club.entity.Games
import net.critika.persistence.club.entity.Lobbies
import net.critika.persistence.club.entity.Tournaments
import net.critika.persistence.user.entity.Users

object Players : KotlinxUUIDTable(name = "players") {
    val userId = reference("user_id", Users).nullable()
    val lobbyId = reference("lobby_id", Lobbies).nullable()
    val tournamentId = reference("tournament_id", Tournaments).nullable()
    val gameId = reference("game_id", Games).nullable()
    val name = varchar("name", 255)
    val status = varchar("status", 255).default(PlayerStatus.WAITING.name)
    val role = varchar("role", 255).nullable().default(PlayerRole.CITIZEN.name)
    val bestMove = integer("best_move").default(0)
    val bonusPoints = integer("bonus_points").default(0)
    val seat = integer("seat").default(0)
    val foulPoints = integer("foul_points").default(0)
}
