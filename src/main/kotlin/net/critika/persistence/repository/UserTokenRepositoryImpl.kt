package net.critika.persistence.repository

import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserToken
import net.critika.domain.user.repository.UserTokenRepository
import net.critika.infrastructure.AES256Util.decrypt
import net.critika.persistence.db.UserTokens
import net.critika.persistence.exception.UserException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserTokenRepositoryImpl : UserTokenRepository {
    suspend fun findByUserId(userId: UUID): UserToken {
        return suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            user.tokens.firstOrNull() ?: throw UserException.NotFound("Token not found")
        }.await()
    }

    suspend fun deleteTokens(userId: UUID) {
        return suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            for (token in user.tokens) {
                token.delete()
                user.tokens.minus(token)
            }
        }.await()
    }

    suspend fun saveToken(userId: UUID, token: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            val userToken = UserToken.new(UUID.randomUUID()) {
                this.userId = user
                this.token = token
            }
            user.tokens.plus(userToken)
            userToken
        }.await()
    }

    suspend fun expireToken(userId: UUID, token: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            val userToken = UserToken.find { UserTokens.token eq token }
                .firstOrNull() ?: throw UserException.NotFound("Token not found")
            user.tokens.minus(userToken)
            userToken.delete()
        }.await()
    }

    suspend fun findByToken(refreshToken: String): UserToken {
        return suspendedTransactionAsync {
            val tokens = UserToken.all()
            val token = tokens.filter {
                decrypt(it.token) == refreshToken
            }
            token.firstOrNull() ?: throw UserException.NotFound("Token not found")
        }.await()
    }
}
