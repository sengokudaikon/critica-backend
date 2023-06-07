package net.critika.application.lobby.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.uuid.UUID
import net.critika.application.game.command.GameCommand
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.command.PlayerCommand
import net.critika.application.player.response.PlayerResponse
import net.critika.domain.club.model.GameStatus
import net.critika.domain.club.repository.GameRepositoryPort
import net.critika.domain.club.repository.LobbyRepositoryPort
import net.critika.domain.gameprocess.repository.PlayerRepositoryPort
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.exception.LobbyException
import net.critika.ports.lobby.LobbyGamePort
import net.critika.ports.lobby.LobbyPlayerPort
import org.koin.core.annotation.Single
import java.time.LocalTime

@Single
class LobbyUseCase(
    private val repository: LobbyRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val gameRepository: GameRepositoryPort,
    private val playerRepository: PlayerRepositoryPort,
) : LobbyGamePort, LobbyPlayerPort {
    override suspend fun addGame(id: UUID, time: LocalTime, host: UUID?): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val date = lobby.date.withHour(time.hour)
            val game = gameRepository.create(GameCommand.Create(date, host))

            require(!lobby.games.contains(game)) { "Game is already in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.players.empty()) { "There shouldn't be any players in game" }

            lobby.games.plus(game)
            return lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun removeGame(id: UUID, gameId: UUID): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val game = gameRepository.get(gameId)

            require(!lobby.games.contains(game)) { "Game is not in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.status != GameStatus.FINISHED) { "Game is in status 'finished'" }
            require(game.status != GameStatus.STARTED) { "Game is in status 'started'" }
            require(game.players.empty()) { "There are players in the game" }

            lobby.games.minus(game)
            require(!lobby.games.contains(game)) { "Game is still in lobby" }

            lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun addPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)

            val user =
                userRepository.findByUsername(playerName) ?: return LobbyException.NotFound("User not found").left()
            val result = playerRepository.create(PlayerCommand.Create(user, playerName, lobby.id.value, null))
            require(!lobby.players.contains(result)) { "Player is already in lobby" }
            lobby.players.plus(result)
            require(!lobby.players.contains(result)) { ("Player not in lobby") }
            lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun addTemporaryPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val result = playerRepository.createTemporaryPlayer(playerName, id, null)
            require(!lobby.players.contains(result)) { "Player is already in lobby" }
            lobby.players.plus(result)
            require(!lobby.players.contains(result)) { "Player not in lobby" }
            lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun addPlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val user = userRepository.findById(playerId) ?: return LobbyException.NotFound("User not found").left()
        val result = playerRepository.create(PlayerCommand.Create(user, user.playerName, lobby.id.value, null))
        lobby.players.plus(result)
        return if (!lobby.players.contains(result)) {
            LobbyException.AlreadyCreated("Player not in lobby").left()
        } else {
            lobby.toResponse().right()
        }
    }

    override suspend fun removePlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val player = playerRepository.getPlayerByName(playerName)
            require(player != null) { "Player not found" }
            require(!lobby.players.contains(player)) { "Player not in lobby" }
            lobby.players.minus(player)
            lobby.toResponse().right()
        } catch (e: Exception) {
            return e.left()
        }
    }

    override suspend fun removePlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val player = playerRepository.get(playerId)
            require(player != null) { "Player not found" }
            lobby.players.minus(player)
            lobby.toResponse().right()
        } catch (e: Exception) {
            return e.left()
        }
    }

    override suspend fun getPlayers(id: UUID): List<PlayerResponse> {
        val lobby = repository.get(id)

        return lobby.players.map { it.toResponse() }
    }

    override suspend fun getGames(id: UUID): List<GameResponse> {
        val games = repository.get(id).games
        return games.map { it.toResponse() }
    }
}
