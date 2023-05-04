package net.critika.usecase.lobby

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.game.command.CreateGame
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.response.PlayerResponse
import net.critika.domain.GameStatus
import net.critika.persistence.exception.LobbyException
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.LobbyRepository
import net.critika.persistence.repository.PlayerRepository
import net.critika.persistence.repository.UserRepositoryImpl
import org.koin.core.annotation.Single
import java.time.LocalTime
import java.util.*

@Single
class LobbyUseCase(
    private val repository: LobbyRepository,
    private val userRepository: UserRepositoryImpl,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend fun addGame(id: UUID, time: LocalTime, host: String?): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val date = lobby.date.withHour(time.hour)
            val game = gameRepository.create(CreateGame(date, host))

            require(!lobby.games.contains(game)) { "Game is already in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.players.empty()) { "There shouldn't be any players in game" }

            lobby.games.plus(game)
            return lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun removeGame(id: UUID, gameId: UUID): Either<Exception, LobbyResponse> {
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

    suspend fun addPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)

            val user =
                userRepository.findByUsername(playerName) ?: return LobbyException.NotFound("User not found").left()
            val result = playerRepository.create(user)
            require(!lobby.players.contains(result)) { "Player is already in lobby" }
            lobby.players.plus(result)
            require(!lobby.players.contains(result)) { ("Player not in lobby") }
            lobby.toResponse().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun addTemporaryPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
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

    suspend fun addPlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val user = userRepository.findById(playerId) ?: return LobbyException.NotFound("User not found").left()
        val result = playerRepository.create(user)
        lobby.players.plus(result)
        return if (!lobby.players.contains(result)) {
            LobbyException.AlreadyCreated("Player not in lobby").left()
        } else {
            lobby.toResponse().right()
        }
    }

    suspend fun removePlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
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

    suspend fun removePlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
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

    suspend fun getPlayers(id: UUID): List<PlayerResponse> {
        val lobby = repository.get(id)

        return lobby.players.map { it.toResponse() }
    }

    suspend fun getGames(id: UUID): List<GameResponse> {
        val games = repository.get(id).games
        return games.map { it.toResponse() }
    }
}
