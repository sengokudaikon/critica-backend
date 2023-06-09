package net.critika.persistence.user.repository

import kotlinx.uuid.UUID
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserDeviceToken
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class UserRepository : UserRepositoryPort {
    override suspend fun findByEmail(email: String): User? {
        return suspendedTransactionAsync { User.find { Users.email eq email }.firstOrNull() }.await()
    }

    override suspend fun findById(id: UUID): User? {
        return suspendedTransactionAsync {
            val user = User.findById(id)
            user
        }.await()
    }

    override suspend fun create(
        uid: String,
        email: String,
        playerName: String,
    ): User {
        return suspendedTransactionAsync {
            User.new {
                this.uid = uid
                this.playerName = playerName
                this.email = email
                this.role = UserRole.USER
                this.created = LocalDateTime.now()
                this.updated = LocalDateTime.now()
            }
        }.await()
    }

    override suspend fun promoteToOwner(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.OWNER
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun promoteToHost(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.HOST
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun updatePlayerName(userId: UUID, playerName: String): User {
        return suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            user.playerName = playerName
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun findByUid(uid: String): User? {
        return suspendedTransactionAsync {
            User.find { Users.uid eq uid }.firstOrNull()
        }.await()
    }

    override suspend fun addDeviceToken(userId: UUID, deviceToken: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            user.deviceTokens.plus(
                UserDeviceToken.new {
                    this.userId = user
                    this.token = deviceToken
                    this.createdAt = LocalDateTime.now()
                    this.expiresAt = LocalDateTime.now().plusYears(1)
                },
            )
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun findByPlayerName(playerName: String): User {
        return suspendedTransactionAsync {
            User.find { Users.playerName eq playerName }.firstOrNull()
        }.await() ?: throw UserException.NotFound("User not found")
    }
}
