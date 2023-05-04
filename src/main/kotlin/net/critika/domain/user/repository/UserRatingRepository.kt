package net.critika.domain.user.repository

import net.critika.domain.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import java.util.*

@Suppress("TooManyFunctions")
interface UserRatingRepository {
    suspend fun createUserRating(userId: UUID): UserRating
    suspend fun findUserRatingById(id: UUID): UserRating?
    suspend fun findUserRatingsByPlayerId(playerId: UUID): List<UserRating>
    suspend fun updateUserRating(userRating: UserRating): UserRating
    suspend fun deleteUserRating(id: UUID): Unit?

    suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic
    suspend fun findRoleStatisticById(id: UUID): RoleStatistic?
    suspend fun findRoleStatisticsByUserRatingId(userRatingId: UUID): List<RoleStatistic>
    suspend fun updateRoleStatistic(roleStatistic: RoleStatistic): RoleStatistic
    suspend fun deleteRoleStatistic(id: UUID): Unit?
    suspend fun findUserRatingsByMonth(month: Int): List<UserRating>
    suspend fun findUserRatingsByYear(year: Int): List<UserRating>
    suspend fun findCurrentSeasonUserRatings(): List<UserRating>
    suspend fun findUserRatingsByWeek(week: Int): List<UserRating>
    suspend fun findUserRatingsByDay(day: Int): List<UserRating>
}
