package io.critica.persistence.repository

import io.critica.domain.UserToken
import io.critica.persistence.db.UserTokens
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.util.UUID

class UserTokenRepository {
    suspend fun findByUserId(userId: UUID): UserToken {
        return suspendedTransactionAsync {
            UserToken.find { UserTokens.userId eq userId }.first()
        }.await()
    }
}
