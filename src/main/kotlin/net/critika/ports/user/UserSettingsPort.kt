package net.critika.ports.user

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.response.UserSettingsResponse
import net.critika.infrastructure.exception.UserException
import net.critika.ports.CrudPort

interface UserSettingsPort : CrudPort<UserSettingsCommand, Either<UserException, UserSettingsResponse>> {
    suspend fun requestPromotion(userId: UUID): Either<UserException, UserSettingsResponse>
    suspend fun getUserRole(userId: UUID): Either<UserException, String>
}
