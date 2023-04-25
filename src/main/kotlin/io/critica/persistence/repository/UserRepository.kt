package io.critica.persistence.repository

import io.critica.domain.User
import io.critica.domain.UserRole
import io.critica.persistence.db.Users
import io.critica.persistence.exception.UserException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserRepository {
    suspend fun findByUsername(playerName: String): User {
        return suspendedTransactionAsync { User.find { Users.playerName eq playerName }.first() }.await()
    }

    suspend fun findById(id: UUID): User {
        return suspendedTransactionAsync {
            val user = User.findById(id)?: throw UserException.NotFound("User not found")
            user
        }.await()
    }

    suspend fun create(userName: String, email: String, password: String): User {
        return suspendedTransactionAsync { User.new {
            this.username = userName
            this.email = email
            this.password = password
            this.role = UserRole.USER
        } }.await()
    }

    suspend fun promoteToOwner(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.OWNER
            user
        }.await()
    }

    suspend fun promoteToAdmin(user: User): User {
        return suspendedTransactionAsync {
            user.role = UserRole.ADMIN
            user
        }.await()
    }
}
