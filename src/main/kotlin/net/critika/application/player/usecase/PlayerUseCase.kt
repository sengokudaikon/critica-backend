package net.critika.application.player.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.uuid.UUID
import net.critika.application.game.command.GameCommand
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.command.PlayerCommand
import net.critika.domain.gameprocess.model.Player
import net.critika.infrastructure.exception.LobbyException
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.club.repository.GameRepository
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.persistence.club.repository.PlayerRepository
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single

@Single
class PlayerUseCase(
    private val playerRepository: PlayerRepository,
    private val lobbyRepository: LobbyRepository,
    private val gameRepository: GameRepository,
) {
    suspend fun getPlayerProfile(name: String): Either<Exception, Player> {
        val player = playerRepository.getPlayerByName(name)

        return player?.right() ?: LobbyException.NotFound("Player not found").left()
    }

    suspend fun getPlayerProfile(id: UUID): Player {
        return playerRepository.get(id)
    }

    suspend fun getPlayerByUser(userId: UUID): Player {
        return playerRepository.getPlayerByUserId(userId) ?: throw UserException.NotFound("Player not found")
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
                playerRepository.update(PlayerCommand.Save(player))
                lobbyRepository.update(LobbyCommand.Save(lobby))

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
                playerRepository.update(PlayerCommand.Save(player))
                gameRepository.update(GameCommand.Save(game))

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
                playerRepository.update(PlayerCommand.Save(player))
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
                playerRepository.update(PlayerCommand.Save(player))
                player.right()
            }
        }.await()
    }
}
