package net.critika.ports.game

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.domain.club.model.Game
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.gameprocess.model.PlayerRole

interface GamePort {
    suspend fun get(id: UUID): GameResponse
    suspend fun list(): List<GameResponse>
    suspend fun assignHost(gameId: UUID, hostId: UUID): GameResponse
    suspend fun start(gameId: UUID): Either<Exception, Game>
    suspend fun addPlayerById(gameId: UUID, playerId: UUID): Either<Exception, Player>
    suspend fun addPlayerByName(gameId: UUID, playerName: String): Either<Exception, Player>
    suspend fun removePlayerById(gameId: UUID, playerId: UUID): Either<Exception, Any?>
    suspend fun finish(gameId: UUID, winningTeam: PlayerRole): Map<PlayerRole, MutableList<Player>>
}
