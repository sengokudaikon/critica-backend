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
import net.critika.domain.user.repository.UserVerificationCodeRepository
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.infrastructure.EmailSender
import net.critika.infrastructure.exception.UserException
import net.critika.ports.user.UserSettingsPort
import org.koin.core.annotation.Single

@Single
class UserSettingsUseCase(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: Argon2PasswordEncoder,
    private val verificationCodeRepository: UserVerificationCodeRepository,
    private val userSettingsRepository: UserSettingsRepositoryPort,
): UserSettingsPort {
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

            is UserSettingsCommand.Update.Username -> {
                userRepository.updateUsername(command.uid, command.username)
                return get(command.uid)
            }

            is UserSettingsCommand.Update.Password -> {
                val user = userRepository.findById(command.uid)
                if (user != null && !passwordEncoder.verify(command.password, user.password)) {
                    userRepository.updatePassword(command.uid, passwordEncoder.encode(command.password))
                }
                return get(command.uid)
            }

            is UserSettingsCommand.Update.Email -> {
                userRepository.updateEmail(command.uid, command.email)
                userSettingsRepository.updateEmailVerificationStatus(command.uid, false)
                requestEmailVerification(command.uid)
                return get(command.uid)
            }

            is UserSettingsCommand.Update.DeviceToken -> {
                userRepository.addDeviceToken(command.uid, command.deviceToken)
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
            is UserSettingsCommand.Update.EmailVerification -> {
                userSettingsRepository.updateEmailVerificationStatus(command.uid, command.emailVerification)
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

    override suspend fun requestEmailVerification(userId: UUID) {
        val user = userRepository.findById(userId)
        if (user != null) {
            val code = generateVerificationCode()
            verificationCodeRepository.createVerificationCode(userId, code)
            try {
                EmailSender.sendVerificationCode(user.email, code)
            } catch (e: Exception) {
                verificationCodeRepository.deleteVerificationCode(userId)
                throw e
            }
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    override suspend fun verifyEmail(userId: UUID, code: String) {
        val verificationCode = verificationCodeRepository.getVerificationCode(userId)
        if (verificationCode.code == code) {
            userSettingsRepository.updateEmailVerificationStatus(userId, true)
            verificationCodeRepository.deleteVerificationCode(userId)
        } else {
            throw UserException.NotFound("Invalid verification code")
        }
    }

    private fun generateVerificationCode(): String {
        val codeLength = 6
        val allowedChars = ('0'..'9')
        return (1..codeLength)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override suspend fun requestPromotion(userId: UUID) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userSettingsRepository.updatePromotion(userId, true)
        } else {
            throw UserException.NotFound("User not found")
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
                user.username,
                settings.emailVerified,
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
                user.username,
                settings.emailVerified,
                settings.publicVisibility,
                settings.pushNotifications,
                settings.language.toString(),
                settings.promotion,
            ).right()
        }
    }

    override suspend fun isEmailVerified(userId: UUID, email: String): Boolean {
        val user = userRepository.findById(userId)
        return if (user != null) {
            userSettingsRepository.isEmailVerified(userId).fold({ false }, { it && user.email == email })
        } else {
            false
        }
    }
}
