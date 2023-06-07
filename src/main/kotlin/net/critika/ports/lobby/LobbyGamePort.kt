package net.critika.ports.lobby

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import java.time.LocalTime

interface LobbyGamePort {
    suspend fun addGame(id: UUID, time: LocalTime, host: UUID?): Either<Exception, LobbyResponse>
    suspend fun removeGame(id: UUID, gameId: UUID): Either<Exception, LobbyResponse>

    suspend fun getGames(id: UUID): List<GameResponse>
}
