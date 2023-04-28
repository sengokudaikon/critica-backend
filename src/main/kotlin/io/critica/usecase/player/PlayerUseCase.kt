package io.critica.usecase.player

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.critica.domain.Player
import io.critica.persistence.exception.LobbyException
import io.critica.persistence.repository.PlayerRepository
import java.util.*

class PlayerUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend fun getPlayerProfile(name: String): Either<Exception, Player> {
        val player = playerRepository.getPlayerByName(name)

        return player?.right() ?: LobbyException.NotFound("Player not found").left()
    }

    suspend fun getPlayerProfile(id: UUID): Player {

        return playerRepository.get(id)
    }
    suspend fun getPlayersInGame(gameId: UUID) = playerRepository.getPlayersInGame(gameId)
}