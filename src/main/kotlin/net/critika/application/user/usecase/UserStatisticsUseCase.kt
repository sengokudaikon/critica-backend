package net.critika.application.user.usecase

import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.user.UserRatingPort
import net.critika.ports.user.UserRoleStatisticPort
import org.koin.core.annotation.Single

@Single
class UserStatisticsUseCase(
    private val userRatingRepository: UserRatingRepositoryPort,
): UserRatingPort, UserRoleStatisticPort {
    override suspend fun createUserRating(userId: UUID): UserRating {
        return userRatingRepository.createUserRating(userId)
    }

    override suspend fun getUserRating(id: UUID): UserRating? {
        return userRatingRepository.findUserRatingById(id)
    }

    override suspend fun getUserRatingsByPlayerId(playerId: UUID): UserRating {
        return userRatingRepository.findUserRatingsByPlayerId(playerId)
    }

    override suspend fun updateUserRating(userRating: UserRating) {
        userRatingRepository.updateUserRating(userRating)
    }

    override suspend fun deleteUserRating(id: UUID) {
        userRatingRepository.deleteUserRating(id)
    }

    override suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic {
        return userRatingRepository.createRoleStatistic(userRatingId, role)
    }

    override suspend fun getRoleStatisticsByUserRatingId(userRatingId: UUID): List<RoleStatistic> {
        return userRatingRepository.findRoleStatisticsByUserRatingId(userRatingId)
    }

    override suspend fun updateRoleStatistic(roleStatistic: RoleStatistic) {
        userRatingRepository.updateRoleStatistic(roleStatistic)
    }

    override suspend fun deleteRoleStatistic(id: UUID) {
        userRatingRepository.deleteRoleStatistic(id)
    }
}
