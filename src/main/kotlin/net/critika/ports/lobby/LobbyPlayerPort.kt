package net.critika.ports.lobby

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.response.PlayerResponse

interface LobbyPlayerPort {
    suspend fun addPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse>
    suspend fun addTemporaryPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse>
    suspend fun addPlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse>
    suspend fun removePlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse>
    suspend fun removePlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse>
    suspend fun getPlayers(id: UUID): List<PlayerResponse>
}