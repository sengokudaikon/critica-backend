package net.critika.persistence.repository

import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepository
import net.critika.persistence.db.Users
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.*

@Single
class UserRepositoryImpl : UserRepository {
    override suspend fun findByUsername(playerName: String): User? {
        return suspendedTransactionAsync { User.find { Users.username eq playerName }.firstOrNull() }.await()
    }

    override suspend fun findByEmail(email: String): User? {
        return suspendedTransactionAsync { User.find { Users.email eq email }.firstOrNull() }.await()
    }

    override suspend fun findById(id: UUID): User? {
        return suspendedTransactionAsync {
            val user = User.findById(id)
            user
        }.await()
    }

    override suspend fun create(userName: String, email: String, playerName: String, password: String): User {
        return suspendedTransactionAsync {
            User.new(UUID.randomUUID()) {
                this.username = userName
                this.playerName = playerName
                this.email = email
                this.password = password
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

    override suspend fun updateUsername(userId: UUID, newUsername: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync null
            user.username = newUsername
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun updatePassword(userId: UUID, newPassword: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync null
            user.password = newPassword
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun updateEmail(userId: UUID, email: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync null
            user.email = email
            user.updated = LocalDateTime.now()
            user
        }.await()
    }

    override suspend fun updatePlayerName(userId: UUID, playerName: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync null
            user.playerName = playerName
            user.updated = LocalDateTime.now()
            user
        }.await()
    }
}
