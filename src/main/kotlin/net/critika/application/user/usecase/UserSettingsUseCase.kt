package net.critika.application.user.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.uuid.UUID
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.response.UserSettingsResponse
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.domain.user.repository.UserSettingsRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.ports.user.UserSettingsPort
import org.koin.core.annotation.Single

@Single
class UserSettingsUseCase(
    private val userRepository: UserRepositoryPort,
    private val userSettingsRepository: UserSettingsRepositoryPort,
) : UserSettingsPort {
    override suspend fun create(command: UserSettingsCommand): Either<UserException, UserSettingsResponse> {
        command as UserSettingsCommand.Create
        val settings = userSettingsRepository.createUserSettings(command.userId, command.language)
        return get(settings.userId.id.value)
    }

    override suspend fun update(command: UserSettingsCommand): Either<UserException, UserSettingsResponse> {
        when (command) {
            is UserSettingsCommand.Update.PlayerName -> {
                userRepository.updatePlayerName(command.uid, command.playerName)
                return get(command.uid)
            }

            is UserSettingsCommand.Update.PushNotification -> {
                userSettingsRepository.updatePushNotifications(command.uid, command.pushNotification)
                return get(command.uid)
            }

            is UserSettingsCommand.Update.Language -> {
                userSettingsRepository.updateLanguage(command.uid, command.language)
                return get(command.uid)
            }

            is UserSettingsCommand.Update.PublicVisibility -> {
                userSettingsRepository.updatePublicVisibility(command.uid, command.publicVisibility)
                return get(command.uid)
            }

            else -> return UserException.NotFound("Invalid command").left()
        }
    }

    override suspend fun delete(id: UUID) {
        userSettingsRepository.deleteUserSettings(id)
    }

    override suspend fun getUserRole(userId: UUID): Either<UserException, String> {
        val user = userRepository.findById(userId)
        return user?.role?.toString()?.right() ?: UserException.NotFound("User not found").left()
    }

    override suspend fun requestPromotion(userId: UUID): Either<UserException, UserSettingsResponse> {
        val user = userRepository.findById(userId)
        return if (user != null) {
            userSettingsRepository.updatePromotion(userId, true)
            get(userId)
        } else {
            UserException.NotFound("User not found").left()
        }
    }

    override suspend fun get(id: UUID): Either<UserException, UserSettingsResponse> {
        val user = userRepository.findById(id)
        return if (user == null) {
            UserException.NotFound("User not found").left()
        } else {
            val settings = userSettingsRepository.getUserSettingsByUserId(id)
            return UserSettingsResponse(
                settings.userId.id.toString(),
                settings.publicVisibility,
                settings.pushNotifications,
                settings.language.toString(),
                settings.promotion,
            ).right()
        }
    }

    override suspend fun list(): List<Either<UserException, UserSettingsResponse>> {
        val users = User.all()
        return users.map { user ->
            val settings = userSettingsRepository.getUserSettingsByUserId(user.id.value)
            UserSettingsResponse(
                settings.userId.id.toString(),
                settings.publicVisibility,
                settings.pushNotifications,
                settings.language.toString(),
                settings.promotion,
            ).right()
        }
    }
}
