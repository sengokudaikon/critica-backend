package io.critica.persistence.repository

import io.critica.domain.User
import io.critica.domain.UserToken
import io.critica.persistence.db.UserTokens
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class UserTokenRepository {
    suspend fun findByUserId(userId: UUID): UserToken {
        return suspendedTransactionAsync {
            UserToken.find { UserTokens.userId eq userId }.first()
        }.await()
    }

    suspend fun deleteTokens(userId: UUID) {
        return suspendedTransactionAsync {
            UserToken.find { UserTokens.userId eq userId }.forEach { it.delete() }
        }.await()
    }

    suspend fun saveToken(userId: UUID, token: String) {
        suspendedTransactionAsync {
            UserToken.new {
                this.userId = User[userId]
                this.token = token
            }
        }.await()
    }
}
