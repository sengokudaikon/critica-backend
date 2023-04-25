package io.critica.domain.events

import io.critica.application.game.response.GameResponse
import io.critica.application.player.response.PlayerResponse
import io.critica.domain.Game

interface Event {
    fun toGameResponse(
        game: Game,
        players: List<PlayerResponse>
    ): GameResponse? {
        var response: GameResponse? = null
        when (this) {
            is DayEvent -> {
                val stageResponse = this.toResponse()
                response = GameResponse(
                    id = game.id.value.toString(),
                    date = game.date,
                    players,
                    stageResponse,
                    stageResponse.candidates,
                    stageResponse.votes
                )
            }

            is NightEvent -> {
                val stageResponse = this.toResponse()
                response = GameResponse(
                    id = game.id.value.toString(),
                    date = game.date,
                    players,
                    stageResponse,
                    mafiaShot = stageResponse.mafiaShot,
                    donCheck = stageResponse.donCheck,
                    detectiveCheck = stageResponse.detectiveCheck
                )
            }
        }
        return response
    }
}
