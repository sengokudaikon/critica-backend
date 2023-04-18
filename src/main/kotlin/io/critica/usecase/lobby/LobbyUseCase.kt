package io.critica.usecase.lobby

import io.critica.application.game.CreateGameRequest
import io.critica.application.game.GameResponse
import io.critica.application.lobby.request.GetLobby
import io.critica.application.lobby.response.LobbyResponse
import io.critica.application.player.PlayerResponse
import io.critica.domain.GameStatus
import io.critica.persistence.exception.GameException
import io.critica.persistence.exception.LobbyException
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.LobbyRepository
import io.critica.persistence.repository.PlayerRepository
import io.critica.persistence.repository.UserRepository
import org.joda.time.DateTime
import org.joda.time.LocalTime

class LobbyUseCase(
    private val repository: LobbyRepository,
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend fun addGame(id: Int, time: LocalTime): LobbyResponse {
        val lobby = repository.get(GetLobby(id))
        val date = DateTime(lobby.date).withTime(time)
        val game = gameRepository.create(CreateGameRequest(date))

        if (lobby.games.contains(game)) throw LobbyException.AlreadyCreated("Game is already in lobby")
        if (game.status != GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
        if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
        if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
        if (!game.players.empty()) throw GameException.TooManyPlayers("There shouldn't be any players in game")

        lobby.games.plus(game)
        return lobby.toResponse()
    }

    suspend fun removeGame(id: Int, gameId: Int): LobbyResponse {
        val lobby = repository.get(GetLobby(id))
        val game = gameRepository.get(gameId)

        if (!lobby.games.contains(game)) throw LobbyException.NotFound("Game is not in lobby")
        if (game.status != GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
        if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
        if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
        if (!game.players.empty()) throw GameException.TooManyPlayers("There are players in the game")

        lobby.games.minus(game)

        if (lobby.games.contains(game)) throw LobbyException.AlreadyCreated("Game is still in lobby")

        return lobby.toResponse()
    }

    suspend fun addPlayer(id: Int, playerName: String): LobbyResponse {
        val lobby = repository.get(GetLobby(id))
        val user = userRepository.findByUsername(playerName)

        val result = playerRepository.create(user)
        lobby.players.plus(result)

        if (!lobby.players.contains(result)) throw LobbyException.AlreadyCreated("Player not in lobby")
        return lobby.toResponse()
    }

    suspend fun addPlayerById(id: Int, playerId: Int): LobbyResponse {
        val lobby = repository.get(GetLobby(id))
        val user = userRepository.findById(playerId)

        val result = playerRepository.create(user)
        lobby.players.plus(result)
        if (!lobby.players.contains(result)) throw LobbyException.AlreadyCreated("Player not in lobby")
        return lobby.toResponse()
    }

    suspend fun removePlayer(id: Int, playerName: String) {
        val lobby = repository.get(GetLobby(id))
        val player = lobby.players.find { it.name == playerName }
        if (player != null) {
            lobby.players.minus(player)
        }
    }

    suspend fun removePlayerById(id: Int, playerId: Int): LobbyResponse {
        val lobby = repository.get(GetLobby(id))
        val player = lobby.players.find { it.id.value == playerId }
        if (player != null) {
            lobby.players.minus(player)
        }

        return lobby.toResponse()
    }

    suspend fun getPlayers(id: Int): List<PlayerResponse> {
        val players = repository.get(GetLobby(id)).players

        return players.map { it.toResponse() }
    }

    suspend fun getGames(id: Int): List<GameResponse> {
        val games = repository.get(GetLobby(id)).games
        return games.map { it.toResponse() }
    }
}
