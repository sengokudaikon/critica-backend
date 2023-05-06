package net.critika.domain.events

import net.critika.application.game.response.GameResponse
import net.critika.application.player.response.PlayerResponse
import net.critika.domain.Game
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.model.NightEvent

interface Event {
    fun toGameResponse(
        game: Game,
        players: List<PlayerResponse>,
        bestMove: List<PlayerResponse>? = null,
    ): GameResponse {
        when (this) {
            is DayEvent -> {
                val stageResponse = this.toResponse()
                return GameResponse(
                    id = game.id.value.toString(),
                    date = game.date.toString(),
                    host = game.host?.toPlayer()?.toResponse(),
                    players,
                    stageResponse,
                    stageResponse.candidates,
                    stageResponse.votes,
                    playersEliminated = game.playersEliminated.map { it.toResponse() },
                    bestMove = bestMove,
                )
            }

            is NightEvent -> {
                val stageResponse = this.toResponse()
                return GameResponse(
                    id = game.id.value.toString(),
                    date = game.date.toString(),
                    host = game.host?.toPlayer()?.toResponse(),
                    players,
                    stageResponse,
                    mafiaShot = stageResponse.mafiaShot,
                    donCheck = stageResponse.donCheck,
                    detectiveCheck = stageResponse.detectiveCheck,
                    playersEliminated = game.playersEliminated.map { it.toResponse() },
                    bestMove = bestMove,
                )
            }

            else -> return GameResponse(
                id = game.id.value.toString(),
                date = game.date.toString(),
                host = game.host?.toPlayer()?.toResponse(),
                players,
                null,
            )
        }
    }
}
