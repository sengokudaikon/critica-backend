package net.critika.usecase.player

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.domain.Player
import net.critika.domain.user.model.User
import net.critika.persistence.exception.LobbyException
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.LobbyRepository
import net.critika.persistence.repository.PlayerRepository
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class PlayerUseCase(
    private val playerRepository: PlayerRepository,
    private val lobbyRepository: LobbyRepository,
    private val gameRepository: GameRepository
) {
    suspend fun getPlayerProfile(name: String): Either<Exception, Player> {
        val player = playerRepository.getPlayerByName(name)

        return player?.right() ?: LobbyException.NotFound("Player not found").left()
    }

    suspend fun getPlayerProfile(id: UUID): Player {

        return playerRepository.get(id)
    }

    suspend fun getPlayerByUser(userId: UUID): Player {
        return playerRepository.getPlayerByUserId(userId) ?: playerRepository.create(user = User[userId])
    }
    suspend fun enterLobby(userId: UUID, lobbyId: UUID?): Either<Exception, LobbyResponse> {
        return suspendedTransactionAsync {
            val player = getPlayerByUser(userId)
            if (player.lobby != null) {
                LobbyException.AlreadyInLobby("Player is already in a lobby").left()
            }

            if (lobbyId != null) {
                val lobby = lobbyRepository.get(lobbyId)

                player.lobby = lobby
                playerRepository.save(player)
                lobbyRepository.save(lobby)

                lobby.toResponse().right()
            } else {
                LobbyException.NotFound("Lobby not found").left()
            }
        }.await()
    }

    suspend fun enterGame(userId: UUID, gameId: UUID?): Either<Exception, GameResponse> {
        return suspendedTransactionAsync {
            val player = getPlayerByUser(userId)
            if (player.game != null) {
                LobbyException.AlreadyInLobby("Player is already in a game").left()
            }

            if (gameId != null) {
                val game = gameRepository.get(gameId)

                player.game = game
                playerRepository.save(player)
                gameRepository.save(game)

                game.toResponse().right()
            } else {
                LobbyException.NotFound("Game not found").left()
            }
        }.await()
    }

    suspend fun quitGame(userId: UUID): Either<Exception, Player> {
        return suspendedTransactionAsync {
            val player = getPlayerByUser(userId)
            if (player.game == null) {
                LobbyException.NotFound("Player is not in a game").left()
            } else {
                player.game = null
                playerRepository.save(player)
                player.right()
            }
        }.await()
    }

    suspend fun exitLobby(userId: UUID, lobbyId: UUID): Either<Exception, Player> {
        return suspendedTransactionAsync {
            val player = getPlayerByUser(userId)
            if (player.lobby == null || player.lobby?.id?.value != lobbyId) {
                LobbyException.NotFound("Player is not in this lobby").left()
            } else {
                player.lobby = null
                playerRepository.save(player)
                player.right()
            }
        }.await()
    }
}
