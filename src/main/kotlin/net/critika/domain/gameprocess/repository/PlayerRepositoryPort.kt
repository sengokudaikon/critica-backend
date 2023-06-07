package net.critika.domain.gameprocess.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.uuid.UUID
import net.critika.application.player.command.PlayerCommand
import net.critika.domain.gameprocess.model.Player
import net.critika.ports.CrudPort

interface PlayerRepositoryPort : CrudPort<PlayerCommand, Player> {
    suspend fun getPlayerByUserIdAndGameId(userId: UUID, gameId: UUID): Player?
    suspend fun getPlayerByPlayerIdAndGameId(playerId: UUID, gameId: UUID): Player?
    suspend fun getPlayerByNameAndGameId(playerName: String, gameId: UUID): Player?
    suspend fun getPlayerByPartialNameAndGameId(playerName: String, gameId: UUID): Player?
    suspend fun getPlayerByName(playerName: String): Player?
    suspend fun createTemporaryPlayer(playerName: String, lobbyId: UUID, gameId: UUID?): Player
    suspend fun getPlayersInGame(gameId: UUID): Flow<Player>
    suspend fun getPlayerByUserId(userId: UUID): Player?
    suspend fun getPlayerInGameBySeat(gameId: UUID, seat: Int): Player
}
