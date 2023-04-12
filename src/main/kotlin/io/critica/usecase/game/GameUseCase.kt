package io.critica.usecase.game

import io.critica.application.game.CreateGameRequest
import io.critica.application.game.GameResponse
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Player
import io.critica.domain.Role
import io.critica.persistence.exception.GameException
import io.critica.persistence.exception.PlayerException
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.LobbyRepository
import io.critica.persistence.repository.PlayerRepository
import io.ktor.server.plugins.*

class GameUseCase(
    private val repository: GameRepository,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository
) {
    suspend fun create(request: CreateGameRequest): GameResponse {
        val game = repository.create(request)
        return GameResponse(game.id.value, game.date, emptyList(), null)
    }

    suspend fun get(gameId: Int): GameResponse {
        val game = repository.get(gameId)
        val players = game.players.map { it.toResponse() }
        val currentStage = game.getCurrentStage()
        val response: GameResponse? = currentStage.toGameResponse(game, players)

        return response ?: GameResponse(
            id = game.id.value,
            date = game.date,
            players,
            null
        )
    }

    suspend fun list(): List<Game> {
        return repository.getGames()
    }

    suspend fun start(gameId: Int): Game {
        val game = repository.get(gameId)
        if (game.status != GameStatus.CREATED) {
            throw BadRequestException("Game is already started")
        }

        val result = repository.update(game, status = GameStatus.STARTED, null)
//        repository.addNightEvent(game, null, null, null)
        return result
    }

    suspend fun addPlayerById( gameId: Int, playerId: Int): Player
    {
        val game = repository.get(gameId)
        val lobby = lobbyRepository.getByGameId(gameId)

        if (!lobby.games.contains(game)) throw GameException.NotInLobby("Game is not in lobby")
        if (game.status == GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
        if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
        if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
        if (game.players.count().toInt() == 10) throw GameException.TooManyPlayers("Too many players in game")

        val player = playerRepository.getPlayerByPlayerIdAndGameId(playerId, gameId)?: throw PlayerException.NotFound("Player not found")
        val result = game.players.plus(player)

        if (result.contains(player)) {
            return player
        } else {
            throw PlayerException.AlreadyInGame("Player is already in game")
        }
    }

    suspend fun addPlayerByName(gameId: Int, playerName: String): Player
    {
        val game = repository.get(gameId)
        val lobby = lobbyRepository.getByGameId(gameId)

        if (!lobby.games.contains(game)) throw GameException.NotInLobby("Game is not in lobby")
        if (game.status == GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
        if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
        if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
        if (game.players.count().toInt() == 10) throw GameException.TooManyPlayers("Too many players in game")

        val player = playerRepository.getPlayerByNameAndGameId(playerName, gameId)

        return if (player != null) {
            game.players.plus(player)
            player
        } else {
            val tempPlayer = playerRepository.createTemporaryPlayer(gameId, playerName)
            game.players.plus(tempPlayer)
            tempPlayer
        }
    }

    suspend fun removePlayerById(gameId: Int, playerId: Int) {
        val player = playerRepository.get(playerId)
        val game = repository.get(gameId)

        val result = game.players.minus(player)

        if (result.contains(player)) {
            throw PlayerException.AlreadyInGame("Player is still in game")
        }
    }

    suspend fun finish(gameId: Int, winningTeam: Role): Int {
        val game = repository.get(gameId)
        repository.update(game, GameStatus.FINISHED, winningTeam)
        //TODO record statistics, return them to controller
        return gameId
    }
}