package io.critica.usecase.game

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.critica.application.game.command.CreateGame
import io.critica.application.game.response.GameResponse
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Player
import io.critica.domain.PlayerRole
import io.critica.persistence.exception.PlayerException
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.LobbyRepository
import io.critica.persistence.repository.PlayerRepository
import io.ktor.server.plugins.*
import org.koin.core.annotation.Single
import java.util.*

private const val MAX_PLAYERS: Int = 10
@Single
class GameUseCase(
    private val repository: GameRepository,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository
) {
    suspend fun create(request: CreateGame): GameResponse {
        val game = repository.create(request)
        return GameResponse(game.id.value.toString(), game.date, emptyList(), null)
    }

    suspend fun get(gameId: UUID): GameResponse {
        val game = repository.get(gameId)
        val players = game.players.map { it.toResponse() }
        val currentStage = game.getCurrentStage()
        val response: GameResponse? = currentStage.toGameResponse(game, players)

        return response ?: GameResponse(
            id = game.id.value.toString(),
            date = game.date,
            players,
            null
        )
    }

    suspend fun list(): List<Game> {
        return repository.getGames()
    }

    suspend fun start(gameId: UUID): Either<Exception, Game> {
        val game = repository.get(gameId)
        return if (game.status != GameStatus.CREATED) {
            BadRequestException("Game is already started").left()
        } else repository.update(game, status = GameStatus.STARTED).right()
    }

    suspend fun addPlayerById( gameId: UUID, playerId: UUID): Either<Exception, Player>
    {
        return try {
            val game = repository.get(gameId)
            val lobby = lobbyRepository.getByGameId(gameId)

            require(!lobby.games.contains(game)) { "Game is not in lobby" }
            require(game.status == GameStatus.WAITING) {"Game is not in status 'waiting'" }
            require (game.status == GameStatus.FINISHED) {"Game is in status 'finished'"}
            require (game.status == GameStatus.STARTED) {"Game is in status 'started'"}
            require (game.players.count().toInt() == MAX_PLAYERS) {"Too many players in game"}

            val player = playerRepository.getPlayerByPlayerIdAndGameId(playerId, gameId)
                requireNotNull(player) { "Player not found" }
            val result = game.players.plus(player)

            require(!result.contains(player)) { "Player is already in game" }

            player.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun addPlayerByName(gameId: UUID, playerName: String): Either<Exception, Player>
    {
        return try {
            val game = repository.get(gameId)
            val lobby = lobbyRepository.getByGameId(gameId)

            require(!lobby.games.contains(game)) { "Game is not in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.status == GameStatus.FINISHED) { "Game is in status 'finished'" }
            require(game.status == GameStatus.STARTED) { "Game is in status 'started'" }
            require(game.players.count().toInt() == MAX_PLAYERS) { "Too many players in game" }

            val player = playerRepository.getPlayerByPartialNameAndGameId(playerName, gameId)

            if (player != null) {
                game.players.plus(player)
                player.right()
            } else {
                val tempPlayer = playerRepository.createTemporaryPlayer(playerName, lobby.id.value, gameId)
                game.players.plus(tempPlayer)
                tempPlayer.right()
            }
        } catch (e: Exception) {
            return e.left()
        }
    }

    suspend fun removePlayerById(gameId: UUID, playerId: UUID): Either<Exception, Any?> {
        val player = playerRepository.get(playerId)
        val game = repository.get(gameId)

        val result = game.players.minus(player)

        return if (result.contains(player)) {
            PlayerException.AlreadyInGame("Player is still in game").left()
        } else null.right()
    }

    suspend fun finish(gameId: UUID, winningTeam: PlayerRole): UUID {
        val game = repository.get(gameId)
        repository.update(game, GameStatus.FINISHED, winningTeam)
        //TODO record statistics, return them to controller
        return gameId
    }
}
