package net.critika.application.tournament.response

import kotlinx.uuid.UUID
import net.critika.application.Response
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.response.PlayerResponse
import net.critika.application.user.response.UserResponse

data class TournamentResponse(
    val id: UUID,
    val name: String,
    val date: String,
    val clubId: UUID?,
    val creator: UserResponse,
    val playerLimit: Int,
    val location: String,
    val description: String,
    val isFinished: Boolean,
    val players: List<PlayerResponse>,
    val lobbies: List<LobbyResponse>,
    val games: List<GameResponse>,
) : Response
