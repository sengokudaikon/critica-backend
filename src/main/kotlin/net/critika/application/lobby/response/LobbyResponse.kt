package net.critika.application.lobby.response

import kotlinx.serialization.Serializable
import net.critika.application.game.response.GameResponse
import net.critika.application.player.response.PlayerResponse

@Serializable
data class LobbyResponse(
    val id: String,
    val date: String,
    val creator: String,
    val games: List<GameResponse> = listOf(),
    val players: List<PlayerResponse> = listOf(),
)
