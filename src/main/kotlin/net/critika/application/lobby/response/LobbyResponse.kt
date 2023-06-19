package net.critika.application.lobby.response

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.application.Response
import net.critika.application.game.response.GameResponse
import net.critika.application.player.response.PlayerResponse
import net.critika.application.user.response.UserResponse

@Serializable
data class LobbyResponse(
    val id: UUID,
    val club: String,
    val date: String,
    val creator: UserResponse,
    val games: List<GameResponse> = listOf(),
    val players: List<PlayerResponse> = listOf(),
) : Response
