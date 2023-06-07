package net.critika.ports.club

import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse

interface ClubLobbyPort {
    suspend fun listLobbies(clubId: UUID): List<LobbyResponse>
    suspend fun listGames(clubId: UUID): List<GameResponse>
}