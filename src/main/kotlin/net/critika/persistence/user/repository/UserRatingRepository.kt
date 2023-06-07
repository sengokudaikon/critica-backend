package net.critika.persistence.user.repository

import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.user.entity.RoleStatistics
import net.critika.persistence.user.entity.UserRatings
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.javatime.day
import org.jetbrains.exposed.sql.javatime.month
import org.jetbrains.exposed.sql.javatime.year
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.joda.time.DateTime
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class UserRatingRepository : UserRatingRepositoryPort {
    override suspend fun createUserRating(userId: UUID): UserRating = suspendedTransactionAsync {
        UserRating.new {
            this.userId = User.findById(userId) ?: throw UserException.NotFound("User not found")
            totalPoints = 0
            bonusPoints = 0
            malusPoints = 0
            bestMovePoints = 0
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
    }.await()

    override suspend fun findUserRatingById(id: UUID): UserRating? = suspendedTransactionAsync {
        UserRating.findById(id)
    }.await()

    override suspend fun findUserRatingsByPlayerId(playerId: UUID): UserRating = suspendedTransactionAsync {
        UserRating.find { UserRatings.userId eq playerId }.firstOrNull()
            ?: throw UserException.NotFound("UserRating not found")
    }.await()

    override suspend fun updateUserRating(userRating: UserRating) = suspendedTransactionAsync {
        userRating.updatedAt = LocalDateTime.now()
        userRating.flush()
        userRating
    }.await()

    override suspend fun deleteUserRating(id: UUID) = suspendedTransactionAsync {
        UserRating.findById(id)?.delete()
    }.await()

    override suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic {
        return suspendedTransactionAsync {
            val userRating = UserRating.findById(userRatingId)
                ?: throw UserException.NotFound("UserRating not found")
            val stat = RoleStatistic.new {
                this.userRatingId = userRating
                this.role = role
                gamesWon = 0
                gamesTotal = 0
                bonusPoints = 0
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }
            stat.flush()
            userRating.roleStatistics.plus(stat)
            userRating.updatedAt = LocalDateTime.now()
            userRating.flush()
            stat
        }.await()
    }

    override suspend fun findRoleStatisticById(id: UUID): RoleStatistic? = suspendedTransactionAsync {
        RoleStatistic.findById(id)
    }.await()

    override suspend fun findRoleStatisticsByUserRatingId(userRatingId: UUID): List<RoleStatistic> {
        return suspendedTransactionAsync {
            RoleStatistic.find { RoleStatistics.userRatingId eq userRatingId }.toList()
        }.await()
    }

    override suspend fun updateRoleStatistic(roleStatistic: RoleStatistic) = suspendedTransactionAsync {
        roleStatistic.updatedAt = LocalDateTime.now()
        roleStatistic.flush()
        roleStatistic
    }.await()

    override suspend fun deleteRoleStatistic(id: UUID) = suspendedTransactionAsync {
        RoleStatistic.findById(id)?.delete()
    }.await()

    override suspend fun findUserRatingsByMonth(month: Int): List<UserRating> {
        return suspendedTransactionAsync {
            UserRatings.select(
                UserRatings.createdAt.month() eq month,
            ).map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findUserRatingsByYear(year: Int): List<UserRating> {
        return suspendedTransactionAsync {
            UserRatings.select(
                UserRatings.createdAt.year() eq year,
            ).map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findCurrentSeasonUserRatings(): List<UserRating> {
        return suspendedTransactionAsync {
            val season = // jan-mar, apr-jun, jul-sep, oct-dec
                when (DateTime.now().monthOfYear) {
                    in 1..3 -> 1
                    in 4..6 -> 2
                    in 7..9 -> 3
                    else -> 4
                }

            // select all user ratings from current season
            UserRatings.select(
                UserRatings.createdAt.month() inList listOf(season * 3 - 2, season * 3 - 1, season * 3),
            ).map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findUserRatingsByDay(day: Int): List<UserRating> {
        return suspendedTransactionAsync {
            UserRatings.select(
                UserRatings.createdAt.day() eq day,
            ).map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findByClubId(clubId: UUID): List<UserRating> {
        return suspendedTransactionAsync {
            val users = Users.select { Users.clubId eq clubId }.map {
                User.findById(it[Users.id].value)!!
            }.toList()
            UserRatings.select {
                UserRatings.userId inList users.map { it.id.value }
            }.withDistinct().map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findUserRatingsByWeek(week: Int): List<UserRating> {
        return suspendedTransactionAsync {
            UserRatings.select(
                UserRatings.createdAt.day() inList listOf(
                    week * 7 - 6,
                    week * 7 - 5,
                    week * 7 - 4,
                    week * 7 - 3,
                    week * 7 - 2,
                    week * 7 - 1,
                    week * 7,
                ),
            ).map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }

    override suspend fun findInRange(from: String, to: String): List<UserRating> {
        return suspendedTransactionAsync {
            UserRatings.select {
                UserRatings.createdAt.between(
                    DateTime.parse(from).toLocalDateTime(),
                    DateTime.parse(to).toLocalDateTime(),
                )
            }.map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }
}
