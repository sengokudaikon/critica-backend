package io.critica.persistence.repository

import io.critica.domain.User
import io.critica.persistence.db.Users
import io.critica.persistence.exception.UserException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class UserRepository {
    suspend fun findByUsername(playerName: String): User {
        return suspendedTransactionAsync { User.find { Users.playerName eq playerName }.first() }.await()
    }

    suspend fun findById(id: Int): User {
        return suspendedTransactionAsync {
            val user = User.findById(id)?: throw UserException.NotFound("User not found")
            user
        }.await()
    }

    suspend fun create(userName: String, password: String): User {
        return suspendedTransactionAsync { User.new { this.username = userName; this.password = password } }.await()
    }
}