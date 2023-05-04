package net.critika.application.stage

import net.critika.application.player.response.PlayerResponse
import net.critika.application.vote.DayVoteResponse

data class DayStageResponse(
    val dayNumber: Int,
    val candidates: List<PlayerResponse>,
    val votes: List<DayVoteResponse>,
) : StageResponse("day")
