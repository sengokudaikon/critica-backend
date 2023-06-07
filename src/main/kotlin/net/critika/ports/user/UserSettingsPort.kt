package net.critika.ports.user

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.response.UserSettingsResponse
import net.critika.infrastructure.exception.UserException
import net.critika.ports.CrudPort

interface UserSettingsPort : CrudPort<UserSettingsCommand, Either<UserException, UserSettingsResponse>> {
    suspend fun requestEmailVerification(userId: UUID)
    suspend fun verifyEmail(userId: UUID, code: String)
    suspend fun requestPromotion(userId: UUID)
    suspend fun isEmailVerified(userId: UUID, email: String): Boolean
}
