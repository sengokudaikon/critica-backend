package net.critika.application.stage

import net.critika.application.player.response.PlayerResponse
import net.critika.application.vote.DayVoteResponse
import net.critika.domain.events.model.DayStage

data class DayStageResponse(
    val stage: DayStage,
    val dayNumber: Int,
    val candidates: List<PlayerResponse>,
    val votes: List<DayVoteResponse>,
) : StageResponse("day")
