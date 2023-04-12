package io.critica.application.stage

import io.critica.application.player.PlayerResponse
import io.critica.application.vote.DayVoteResponse
import io.critica.application.vote.PlayerVoteResponse

data class DayStageResponse(
    val dayNumber: Int,
    val candidates: List<PlayerResponse>,
    val votes: List<DayVoteResponse>,
) : StageResponse("day")