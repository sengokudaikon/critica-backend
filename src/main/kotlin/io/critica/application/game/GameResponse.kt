package io.critica.application.game

import io.critica.application.player.PlayerResponse
import io.critica.application.stage.StageResponse
import io.critica.application.vote.DayVoteResponse

data class GameResponse(
    val id: String,
    val date: String,
    val players: List<PlayerResponse>,
    val currentStage: StageResponse? = null,
    val nominates: List<PlayerResponse> = listOf(),
    val votes: List<DayVoteResponse> = listOf(),
    val mafiaShot: PlayerResponse? = null,
    val detectiveCheck: PlayerResponse? = null,
    val donCheck: PlayerResponse? = null,
    val playersEliminated: List<PlayerResponse> = listOf()
)
