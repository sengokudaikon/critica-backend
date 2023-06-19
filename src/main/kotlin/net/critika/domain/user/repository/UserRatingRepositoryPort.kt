package net.critika.domain.user.repository

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRatingCommand
import net.critika.domain.user.model.UserRating
import net.critika.ports.CrudPort

interface UserRatingRepositoryPort : CrudPort<UserRatingCommand, UserRating> {
    suspend fun findByUser(userId: UUID): List<UserRating>
    suspend fun findUserRatingsByPlayerId(playerId: UUID): UserRating
    suspend fun findByClubId(clubId: UUID): List<UserRating>
    suspend fun findByTournament(tournamentId: UUID): List<UserRating>
}
