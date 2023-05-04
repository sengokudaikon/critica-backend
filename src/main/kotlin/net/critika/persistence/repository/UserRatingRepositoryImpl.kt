package net.critika.persistence.repository

import net.critika.domain.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepository
import net.critika.persistence.db.RoleStatistics
import net.critika.persistence.db.UserRatings
import net.critika.persistence.exception.UserException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.`java-time`.day
import org.jetbrains.exposed.sql.`java-time`.month
import org.jetbrains.exposed.sql.`java-time`.year
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.joda.time.DateTime
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserRatingRepositoryImpl : UserRatingRepository {
    override suspend fun createUserRating(userId: UUID): UserRating = suspendedTransactionAsync {
        UserRating.new {
            this.userId = User.findById(userId) ?: throw UserException.NotFound("User not found")
            totalPoints = 0
            bonusPoints = 0
            malusPoints = 0
            bestMovePoints = 0
        }
    }.await()

    override suspend fun findUserRatingById(id: UUID): UserRating? = suspendedTransactionAsync {
        UserRating.findById(id)
    }.await()

    override suspend fun findUserRatingsByPlayerId(playerId: UUID): List<UserRating> = suspendedTransactionAsync {
        UserRating.find { UserRatings.userId eq playerId }.toList()
    }.await()

    override suspend fun updateUserRating(userRating: UserRating) = suspendedTransactionAsync {
        userRating.flush()
        userRating
    }.await()

    override suspend fun deleteUserRating(id: UUID) = suspendedTransactionAsync {
        UserRating.findById(id)?.delete()
    }.await()

    override suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic {
        return suspendedTransactionAsync {
            RoleStatistic.new {
                this.userRatingId = UserRating.findById(userRatingId)
                    ?: throw UserException.NotFound("UserRating not found")
                this.role = role
                gamesWon = 0
                gamesTotal = 0
                bonusPoints = 0
            }
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
}
