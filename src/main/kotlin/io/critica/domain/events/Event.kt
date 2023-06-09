package io.critica.domain.events

import io.critica.application.game.GameResponse
import io.critica.application.player.PlayerResponse
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
                    id = game.id.value,
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
        return response
    }
}
