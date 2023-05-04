package net.critika.domain.user.repository

import arrow.core.Either
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserSetting
import org.koin.core.annotation.Single
import java.util.*

interface UserSettingsRepository {
    suspend fun updateEmailVerificationStatus(userId: UUID, emailVerified: Boolean)
    suspend fun updatePromotion(userId: UUID, b: Boolean?)
    suspend fun findUsersRequestingPromotion(): List<User>
    suspend fun updatePublicVisibility(userId: UUID, b: Boolean)
    suspend fun updatePushNotifications(userId: UUID, b: Boolean)
    suspend fun updateLanguage(userId: UUID, language: String)
    suspend fun isEmailVerified(userId: UUID): Either<Throwable, Boolean>
    suspend fun getUserSettingsByUserId(userId: UUID): UserSetting
    suspend fun createUserSettings(userId: UUID, language: String? = null): UserSetting
}