package net.critika.usecase.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.response.UserSettings
import net.critika.domain.user.model.UserSetting
import net.critika.domain.user.repository.UserRepository
import net.critika.domain.user.repository.UserSettingsRepository
import net.critika.domain.user.repository.UserVerificationCodeRepository
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.infrastructure.EmailSender
import net.critika.persistence.exception.UserException
import org.koin.core.annotation.Single
import java.util.*

@Single
class UserSettingsUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: Argon2PasswordEncoder,
    private val verificationCodeRepository: UserVerificationCodeRepository,
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend fun createUserSettings(userId: UUID, language: String? = null): UserSetting {
        return userSettingsRepository.createUserSettings(userId, language)
    }

    suspend fun setPlayerName(userId: UUID, playerName: String) {
        userRepository.updatePlayerName(userId, playerName)
    }

    suspend fun changeUsername(userId: UUID, newUsername: String) {
        userRepository.updateUsername(userId, newUsername)
    }

    suspend fun changePassword(userId: UUID, newPassword: String) {
        val user = userRepository.findById(userId)
        if (user != null && !passwordEncoder.verify(newPassword, user.password)) {
            userRepository.updatePassword(userId, passwordEncoder.encode(newPassword))
        } else {
            throw UserException.NotFound("Incorrect old password")
        }
    }

    suspend fun requestEmailVerification(userId: UUID) {
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

    suspend fun verifyEmail(userId: UUID, code: String) {
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

    suspend fun changeEmail(userId: UUID, email: String) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userRepository.updateEmail(userId, email)
            userSettingsRepository.updateEmailVerificationStatus(userId, false)
            requestEmailVerification(userId)
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    suspend fun requestPromotion(userId: UUID) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userSettingsRepository.updatePromotion(userId, true)
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    suspend fun changeLanguage(userId: UUID, language: String) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userSettingsRepository.updateLanguage(userId, language)
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    suspend fun changePushNotifications(userId: UUID, pushNotifications: Boolean) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userSettingsRepository.updatePushNotifications(userId, pushNotifications)
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    suspend fun changePublicVisibility(userId: UUID, publicVisibility: Boolean) {
        val user = userRepository.findById(userId)
        if (user != null) {
            userSettingsRepository.updatePublicVisibility(userId, publicVisibility)
        } else {
            throw UserException.NotFound("User not found")
        }
    }

    suspend fun getUserSettings(userId: UUID): Either<Exception, UserSettings> {
        val user = userRepository.findById(userId)
        return if (user == null) {
            UserException.NotFound("User not found").left()
        } else {
            val settings = userSettingsRepository.getUserSettingsByUserId(userId)
            return UserSettings(
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

    suspend fun isEmailVerified(userId: UUID, email: String): Boolean {
        val user = userRepository.findById(userId)
        return if (user != null) {
            userSettingsRepository.isEmailVerified(userId).fold({ false }, { it && user.email == email })
        } else {
            false
        }
    }
}
