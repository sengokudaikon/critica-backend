package io.critica.usecase.lobby

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.critica.application.game.CreateGameRequest
import io.critica.application.game.GameResponse
import io.critica.application.lobby.response.LobbyResponse
import io.critica.application.player.PlayerResponse
import io.critica.domain.GameStatus
import io.critica.persistence.exception.LobbyException
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.LobbyRepository
import io.critica.persistence.repository.PlayerRepository
import io.critica.persistence.repository.UserRepository
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.koin.core.annotation.Single
import java.util.*

@Single
class LobbyUseCase(
    private val repository: LobbyRepository,
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend fun addGame(id: UUID, time: LocalTime): Either<Exception, LobbyResponse> {
        return try {
            val lobby = repository.get(id)
            val date = DateTime(lobby.date).withTime(time)
            val game = gameRepository.create(CreateGameRequest(date))

            require(lobby.games.contains(game)) {"Game is already in lobby"}
            require(game.status != GameStatus.WAITING) {  "Game is not in status 'waiting'"}
            require(game.status == GameStatus.FINISHED) {  "Game is in status 'finished'" }
            require(game.status == GameStatus.STARTED) { "Game is in status 'started'" }
            require(!game.players.empty()) { "There shouldn't be any players in game" }

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

            require(lobby.games.contains(game)) { "Game is not in lobby" }
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
        val lobby = repository.get(id)

        val user = userRepository.findByUsername(playerName)
        val result = playerRepository.create(user)
        lobby.players.plus(result)
        return if (!lobby.players.contains(result)) {
            LobbyException.AlreadyCreated("Player not in lobby").left()
        } else lobby.toResponse().right()
    }

    suspend fun addTemporaryPlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val result = playerRepository.createTemporaryPlayer(playerName, id, null)
        lobby.players.plus(result)
        return if (!lobby.players.contains(result)) LobbyException.AlreadyCreated("Player not in lobby").left()
        else lobby.toResponse().right()
    }

    suspend fun addPlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val user = userRepository.findById(playerId)

        val result = playerRepository.create(user)
        lobby.players.plus(result)
        return if (!lobby.players.contains(result)) LobbyException.AlreadyCreated("Player not in lobby").left()
        else lobby.toResponse().right()
    }

    suspend fun removePlayer(id: UUID, playerName: String): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val player = lobby.players.find { it.name == playerName }
        return if (player != null) {
            lobby.players.minus(player)
            lobby.toResponse().right()
        } else LobbyException.NotFound("Player not found").left()
    }

    suspend fun removePlayerById(id: UUID, playerId: UUID): Either<Exception, LobbyResponse> {
        val lobby = repository.get(id)
        val player = lobby.players.find { it.id.value == playerId }
        return if (player != null) {
            lobby.players.minus(player)
            lobby.toResponse().right()
        } else LobbyException.NotFound("Player not found").left()
    }

    suspend fun getPlayers(id: UUID): List<PlayerResponse> {
        val players = repository.get(id).players

        return players.map { it.toResponse() }
    }

    suspend fun getGames(id: UUID): List<GameResponse> {
        val games = repository.get(id).games
        return games.map { it.toResponse() }
    }
}
