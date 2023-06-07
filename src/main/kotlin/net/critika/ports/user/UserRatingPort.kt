package net.critika.ports.user

import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating

interface UserRatingPort {
    suspend fun createUserRating(userId: UUID): UserRating
    suspend fun getUserRating(id: UUID): UserRating?
    suspend fun getUserRatingsByPlayerId(playerId: UUID): UserRating
    suspend fun updateUserRating(userRating: UserRating)
    suspend fun deleteUserRating(id: UUID)
}