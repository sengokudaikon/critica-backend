package io.critica.application.stage

import io.critica.application.player.response.PlayerResponse
import io.critica.application.vote.DayVoteResponse

data class DayStageResponse(
    val dayNumber: Int,
    val candidates: List<PlayerResponse>,
    val votes: List<DayVoteResponse>,
) : StageResponse("day")
