package io.critica.application.vote

import io.critica.application.player.PlayerResponse

data class DayVoteResponse(
    val dayNumber: Int,
    val target: PlayerResponse,
    val player: PlayerResponse,
)
