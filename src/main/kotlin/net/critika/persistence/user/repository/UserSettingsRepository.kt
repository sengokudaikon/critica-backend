package net.critika.persistence.user.repository

import arrow.core.Either
import arrow.core.right
import kotlinx.uuid.UUID
import net.critika.domain.user.model.Language
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserSetting
import net.critika.domain.user.repository.UserSettingsRepositoryPort
import net.critika.infrastructure.exception.UserException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.joda.time.DateTime
import org.koin.core.annotation.Single

@Single
class UserSettingsRepository : UserSettingsRepositoryPort {
    override suspend fun updateEmailVerificationStatus(userId: UUID, emailVerified: Boolean) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.emailVerified = emailVerified
            userSettings.updatedAt = DateTime.now().millis
        }.await()
    }

    override suspend fun updatePromotion(userId: UUID, b: Boolean?) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.promotion = b
            userSettings.updatedAt = DateTime.now().millis
        }.await()
    }

    override suspend fun findUsersRequestingPromotion(): List<User> {
        return suspendedTransactionAsync {
            User.all().filter { user ->
                val userSettings = user.settings.singleOrNull()
                userSettings?.promotion == true
            }
        }.await()
    }

    override suspend fun updatePublicVisibility(userId: UUID, b: Boolean) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.publicVisibility = b
            userSettings.updatedAt = DateTime.now().millis
        }.await()
    }

    override suspend fun updatePushNotifications(userId: UUID, b: Boolean) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.pushNotifications = b
            userSettings.updatedAt = DateTime.now().millis
        }.await()
    }

    override suspend fun updateLanguage(userId: UUID, language: String) {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.language = Language.valueOf(language)
            userSettings.updatedAt = DateTime.now().millis
        }.await()
    }

    override suspend fun isEmailVerified(userId: UUID): Either<Throwable, Boolean> {
        suspendedTransactionAsync {
            val user = User.findById(userId) ?: return@suspendedTransactionAsync
            val userSettings = user.settings.singleOrNull() ?: return@suspendedTransactionAsync
            userSettings.emailVerified.right()
        }.await()

        return Either.Left(UserException.NotFound("User not found"))
    }

    override suspend fun getUserSettingsByUserId(userId: UUID): UserSetting {
        return suspendedTransactionAsync {
            val user = User.findById(userId) ?: throw UserException.NotFound("User not found")
            user.settings.singleOrNull() ?: throw UserException.NotFound("User settings not found")
        }.await()
    }

    override suspend fun createUserSettings(userId: UUID, language: String?): UserSetting {
        return suspendedTransactionAsync {
            val userSetting = UserSetting.new {
                this.userId = User.findById(userId) ?: throw UserException.NotFound("User not found")
                this.emailVerified = false
                this.publicVisibility = false
                this.pushNotifications = false
                this.language = language?.let { Language.valueOf(it) } ?: Language.ENGLISH
                this.promotion = false
                this.createdAt = DateTime.now().millis
                this.updatedAt = DateTime.now().millis
            }
            userSetting
        }.await()
    }

    override suspend fun deleteUserSettings(id: UUID) {
        suspendedTransactionAsync {
            val userSetting = UserSetting.findById(id) ?: throw UserException.NotFound("User settings not found")
            userSetting.delete()
        }.await()
    }
}