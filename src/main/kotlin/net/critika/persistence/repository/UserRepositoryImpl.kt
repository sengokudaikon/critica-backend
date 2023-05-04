package net.critika.persistence.repository

import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepository
import net.critika.persistence.db.Users
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserRepositoryImpl : UserRepository {
    override suspend fun findByUsername(playerName: String): User? {
        return suspendedTransactionAsync { User.find { Users.playerName eq playerName }.firstOrNull() }.await()
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

    override suspend fun create(userName: String, email: String, password: String): User {
        return suspendedTransactionAsync {
            User.new(UUID.randomUUID()) {
                this.username = userName
                this.email = email
                this.password = password
                this.role = UserRole.USER
            }
        }.await()
    }

    override suspend fun promoteToOwner(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.OWNER
            user
        }.await()
    }

    override suspend fun promoteToAdmin(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.ADMIN
            user
        }.await()
    }

    override suspend fun updateUsername(userId: UUID, newUsername: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId)
            user?.username = newUsername
            user
        }.await()
    }

    override suspend fun updatePassword(userId: UUID, newPassword: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId)
            user?.password = newPassword
            user
        }.await()
    }

    override suspend fun updateEmail(userId: UUID, email: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId)
            user?.email = email
            user
        }.await()
    }
}
