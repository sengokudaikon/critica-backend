package net.critika.ports.user

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRatingCommand
import net.critika.application.user.response.RatingResponse
import net.critika.ports.CrudPort
import net.critika.ports.club.RatingPort

interface UserRatingPort : CrudPort<UserRatingCommand, RatingResponse>, RatingPort<UUID> {
    suspend fun getUserRatingsByPlayerId(playerId: UUID): RatingResponse
}
