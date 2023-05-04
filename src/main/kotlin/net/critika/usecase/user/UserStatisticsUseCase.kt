package net.critika.usecase.user

import net.critika.domain.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepository
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserStatisticsUseCase(
    private val userRatingRepository: UserRatingRepository,
) {
    suspend fun createUserRating(userId: UUID): UserRating {
        return userRatingRepository.createUserRating(userId)
    }

    suspend fun getUserRating(id: UUID): UserRating? {
        return userRatingRepository.findUserRatingById(id)
    }

    suspend fun getUserRatingsByPlayerId(playerId: UUID): List<UserRating> {
        return userRatingRepository.findUserRatingsByPlayerId(playerId)
    }

    suspend fun updateUserRating(userRating: UserRating) {
        userRatingRepository.updateUserRating(userRating)
    }

    suspend fun deleteUserRating(id: UUID) {
        userRatingRepository.deleteUserRating(id)
    }

    suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic {
        return userRatingRepository.createRoleStatistic(userRatingId, role)
    }

    suspend fun getRoleStatistic(id: UUID): RoleStatistic? {
        return userRatingRepository.findRoleStatisticById(id)
    }

    suspend fun getRoleStatisticsByUserRatingId(userRatingId: UUID): List<RoleStatistic> {
        return userRatingRepository.findRoleStatisticsByUserRatingId(userRatingId)
    }

    suspend fun updateRoleStatistic(roleStatistic: RoleStatistic) {
        userRatingRepository.updateRoleStatistic(roleStatistic)
    }

    suspend fun deleteRoleStatistic(id: UUID) {
        userRatingRepository.deleteRoleStatistic(id)
    }

    suspend fun getRatingForMonth(month: Int): List<UserRating> {
        return userRatingRepository.findUserRatingsByMonth(month)
    }

    suspend fun getRatingForWeek(week: Int): List<UserRating> {
        return userRatingRepository.findUserRatingsByWeek(week)
    }

    suspend fun getRatingForDay(day: Int): List<UserRating> {
        return userRatingRepository.findUserRatingsByDay(day)
    }

    suspend fun getRatingForYear(year: Int): List<UserRating> {
        return userRatingRepository.findUserRatingsByYear(year)
    }

    suspend fun getRatingForSeason(): List<UserRating> {
        return userRatingRepository.findCurrentSeasonUserRatings()
    }
}