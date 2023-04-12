package io.critica.persistence.repository

import io.critica.domain.UserToken
import io.critica.persistence.db.UserTokens
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class UserTokenRepository {
    suspend fun findByUserId(userId: Int?): UserToken {
        return suspendedTransactionAsync {
            UserToken.find { UserTokens.userId eq userId }.first()
        }.await()
    }
}