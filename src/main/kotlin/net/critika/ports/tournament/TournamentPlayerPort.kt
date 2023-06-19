package net.critika.ports.tournament

import kotlinx.uuid.UUID
import net.critika.application.player.response.PlayerResponse
import net.critika.application.tournament.response.TournamentResponse
import net.critika.application.user.response.RatingResponse

interface TournamentPlayerPort {
    suspend fun getPlayers(tournamentId: UUID): List<PlayerResponse>
    suspend fun join(tournamentId: UUID, userId: UUID): TournamentResponse
    suspend fun leave(tournamentId: UUID, userId: UUID): TournamentResponse
    suspend fun getRating(id: UUID): RatingResponse
}
