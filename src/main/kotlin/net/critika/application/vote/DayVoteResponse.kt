package net.critika.application.vote

import kotlinx.serialization.Serializable
import net.critika.application.player.response.PlayerResponse

@Serializable
data class DayVoteResponse(
    val dayNumber: Int,
    val target: PlayerResponse,
    val player: PlayerResponse,
)
