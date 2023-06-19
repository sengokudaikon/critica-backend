package net.critika.persistence.user.repository

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRatingCommand
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.gameprocess.entity.Players
import net.critika.persistence.user.entity.UserRatings
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class UserRatingRepository : UserRatingRepositoryPort {
    override suspend fun findByUser(userId: UUID): List<UserRating> {
        return suspendedTransactionAsync {
            UserRating.find { UserRatings.userId eq userId }.toList()
        }.await()
    }

    override suspend fun create(command: UserRatingCommand): UserRating = suspendedTransactionAsync {
        command as UserRatingCommand.Create
        UserRating.new {
            this.userId = User.findById(command.userId) ?: throw UserException.NotFound("User not found")
            totalPoints = 0
            bonusPoints = 0
            malusPoints = 0
            bestMovePoints = 0
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
    }.await()

    override suspend fun get(id: UUID): UserRating = suspendedTransactionAsync {
        UserRating.findById(id) ?: throw UserException.NotFound("UserRating not found")
    }.await()

    override suspend fun list(): List<UserRating> {
        return suspendedTransactionAsync {
            UserRating.all().toList()
        }.await()
    }

    override suspend fun findUserRatingsByPlayerId(playerId: UUID): UserRating = suspendedTransactionAsync {
        UserRating.find { UserRatings.userId eq playerId }.firstOrNull()
            ?: throw UserException.NotFound("UserRating not found")
    }.await()

    override suspend fun update(command: UserRatingCommand) = suspendedTransactionAsync {
        command as UserRatingCommand.Update
        command.userRating.updatedAt = LocalDateTime.now()
        command.userRating.flush()
        command.userRating
    }.await()

    override suspend fun delete(id: UUID): Unit = suspendedTransactionAsync {
        UserRating.findById(id)?.delete() ?: throw UserException.NotFound("UserRating not found")
    }.await()

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

    override suspend fun findByTournament(tournamentId: UUID): List<UserRating> {
        return suspendedTransactionAsync {
            val users = Player.find { Players.tournamentId eq tournamentId }.toList().map { it.user!! }
            UserRatings.select {
                UserRatings.userId inList users.map { it.id.value }
            }.withDistinct().map {
                UserRating.findById(it[UserRatings.id].value)!!
            }.toList()
        }.await()
    }
}
