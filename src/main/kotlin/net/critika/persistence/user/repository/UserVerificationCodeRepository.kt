package net.critika.persistence.user.repository

import kotlinx.uuid.UUID
import net.critika.domain.user.model.User
import net.critika.domain.user.model.VerificationCode
import net.critika.domain.user.repository.UserVerificationCodeRepository
import net.critika.persistence.user.entity.UserVerificationCodes
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single

@Single
class UserVerificationCodeRepository : UserVerificationCodeRepository {
    override suspend fun createVerificationCode(userId: UUID, code: String) {
        suspendedTransactionAsync {
            VerificationCode.new {
                this.userId = User[userId]
                this.code = code
            }
        }.await()
    }

    override suspend fun getVerificationCode(userId: UUID): VerificationCode {
        return suspendedTransactionAsync {
            VerificationCode.find { UserVerificationCodes.userId eq userId }.first()
        }.await()
    }

    override suspend fun deleteVerificationCode(userId: UUID) {
        return suspendedTransactionAsync {
            VerificationCode.find { UserVerificationCodes.userId eq userId }.first().delete()
        }.await()
    }
}