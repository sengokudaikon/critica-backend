package net.critika.application.club.response

import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.user.response.UserResponse

data class ClubResponse(
    val id: String,
    val name: String,
    val creator: UserResponse,
    val members: List<UserResponse> = listOf(),
    val lobbies: List<LobbyResponse>,
    val games: List<GameResponse>,
    val createdAt: String,
    val updatedAt: String,
    val country: String,
    val city: String,
    val address: String,
    val logo: String,
    val description: String,
    val ruleSet: String,
)
