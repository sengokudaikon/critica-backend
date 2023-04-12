package io.critica.presentation.action.game

import io.critica.application.game.CreateGameRequest
import io.critica.application.game.GameResponse
import io.critica.application.stage.DayStageResponse
import io.critica.application.stage.NightStageResponse
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Role
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.UserRepository
import io.critica.presentation.controller.GameController.Companion.toResponse
import io.ktor.server.plugins.*

class Game(
    private val repository: GameRepository,
    private val userRepo: UserRepository
) {
    suspend fun create(request: CreateGameRequest): GameResponse {
        val game = repository.create(request)
        return GameResponse(game.id.value, game.date, emptyList(), null)
    }

    suspend fun get(gameId: Int): GameResponse {
        val game = repository.get(gameId)
        val players = game.players.map { it.toResponse() }
        val currentStage = game.getCurrentStage()
        var response: GameResponse? = null
        if (currentStage is DayStageResponse) {
            response = GameResponse(
                id = game.id.value,
                date = game.date,
                players,
                currentStage,
                currentStage.candidates,
                currentStage.votes
            )
        }

        if (currentStage is NightStageResponse) {
            response = GameResponse(
                id = game.id.value,
                date = game.date,
                players,
                currentStage,
                mafiaShot = currentStage.mafiaShot,
                donCheck = currentStage.donCheck,
                detectiveCheck = currentStage.detectiveCheck
            )
        }

        return response!!
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

    suspend fun finish(gameId: Int, winningTeam: Role): Int {
        val game = repository.get(gameId)
        repository.update(game, GameStatus.FINISHED, winningTeam)
        //TODO record statistics, return them to controller
        return gameId
    }
}