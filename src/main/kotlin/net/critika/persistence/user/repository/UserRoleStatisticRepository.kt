package net.critika.persistence.user.repository

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRoleStatisticsCommand
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRoleStatisticRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.user.entity.RoleStatistics
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class UserRoleStatisticRepository : UserRoleStatisticRepositoryPort {
    override suspend fun create(command: UserRoleStatisticsCommand): RoleStatistic {
        command as UserRoleStatisticsCommand.Create
        return suspendedTransactionAsync {
            val userRating = UserRating.findById(command.ratingId)
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

    override suspend fun list(): List<RoleStatistic> {
        return suspendedTransactionAsync {
            RoleStatistic.all().toList()
        }.await()
    }

    override suspend fun get(id: UUID): RoleStatistic = suspendedTransactionAsync {
        RoleStatistic.findById(id) ?: throw UserException.NotFound("RoleStatistic not found")
    }.await()

    override suspend fun findByUserRating(id: UUID): List<RoleStatistic> {
        return suspendedTransactionAsync {
            RoleStatistic.find { RoleStatistics.userRatingId eq id }.toList()
        }.await()
    }

    override suspend fun update(command: UserRoleStatisticsCommand): RoleStatistic {
        command as UserRoleStatisticsCommand.Update
        return suspendedTransactionAsync {
            command.roleStatistic.updatedAt = LocalDateTime.now()
            command.roleStatistic.flush()
            command.roleStatistic
        }.await()
    }

    override suspend fun delete(id: UUID): Unit = suspendedTransactionAsync {
        RoleStatistic.findById(id)?.delete() ?: throw UserException.NotFound("RoleStatistic not found")
    }.await()
}
