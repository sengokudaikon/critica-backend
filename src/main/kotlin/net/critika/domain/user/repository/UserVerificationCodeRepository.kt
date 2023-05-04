package net.critika.domain.user.repository

import net.critika.domain.user.model.VerificationCode
import java.util.*

interface UserVerificationCodeRepository {
    suspend fun createVerificationCode(userId: UUID, code: String)
    suspend fun getVerificationCode(userId: UUID): VerificationCode
    suspend fun deleteVerificationCode(userId: UUID)
}