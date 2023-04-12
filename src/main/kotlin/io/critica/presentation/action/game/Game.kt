package io.critica.presentation.action.game

import io.critica.application.game.CreateGameRequest
import io.critica.application.game.GameResponse
import io.critica.application.player.PlayerResponse
import io.critica.application.stage.DayStageResponse
import io.critica.application.stage.NightStageResponse
import io.critica.application.stage.StageResponse
import io.critica.application.vote.DayVoteResponse
import io.critica.application.vote.PlayerVoteResponse
import io.critica.domain.*
import io.critica.domain.Game
import io.critica.domain.events.*
import io.critica.domain.events.Event
import io.critica.persistence.GameProcessor
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.UserRepository
import io.critica.presentation.controller.GameController.Companion.toResponse
import io.ktor.server.plugins.*
import org.koin.java.KoinJavaComponent.inject

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
        when (currentStage) {
            is DayEvent -> {
                val stageResponse = currentStage.toResponse()
                response = GameResponse(
                    id = game.id.value,
                    date = game.date,
                    players,
                    stageResponse,
                    stageResponse.candidates,
                    stageResponse.votes
                )
            }

            is NightEvent -> {
                val stageResponse = currentStage.toResponse()
                response = GameResponse(
                    id = game.id.value,
                    date = game.date,
                    players,
                    stageResponse,
                    mafiaShot = stageResponse.mafiaShot,
                    donCheck = stageResponse.donCheck,
                    detectiveCheck = stageResponse.detectiveCheck
                )
            }
        }

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
        GameProcessor.Start(game) //this is a dummy call to make sure the dependencies are injected
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

private fun DayEvent.toResponse(): DayStageResponse
{
    return DayStageResponse(
        dayNumber = this.day,
        candidates = this.candidates.map { it.toResponse() },
        votes = this.votes.map { it.toResponse() }
    )
}

private fun DayCandidate.toResponse(): PlayerResponse {
    return PlayerResponse(
        id = this.player.id.value,
        name = this.player.name,
        alive = this.player.status == PlayerStatus.ALIVE.toString(),
    )
}

private fun DayVote.toResponse(): DayVoteResponse {
    return DayVoteResponse(
        dayNumber = this.day.day,
        player = this.player.toResponse(),
        target = this.target.toResponse(),
    )
}

private fun NightEvent.toResponse(): NightStageResponse {
    return NightStageResponse(
        dayNumber = this.night,
        mafiaShot = this.mafiaShot?.let { Player[it].toResponse() },
        detectiveCheck = this.detectiveCheck?.let { Player[it].toResponse() },
        donCheck = this.donCheck?.let { Player[it].toResponse() }
    )
}