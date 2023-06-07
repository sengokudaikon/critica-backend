package net.critika.domain.user.repository

import arrow.core.Either
import kotlinx.uuid.UUID
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserSetting

interface UserSettingsRepositoryPort {
    suspend fun updateEmailVerificationStatus(userId: UUID, emailVerified: Boolean)
    suspend fun updatePromotion(userId: UUID, b: Boolean?)
    suspend fun findUsersRequestingPromotion(): List<User>
    suspend fun updatePublicVisibility(userId: UUID, b: Boolean)
    suspend fun updatePushNotifications(userId: UUID, b: Boolean)
    suspend fun updateLanguage(userId: UUID, language: String)
    suspend fun isEmailVerified(userId: UUID): Either<Throwable, Boolean>
    suspend fun getUserSettingsByUserId(userId: UUID): UserSetting
    suspend fun createUserSettings(userId: UUID, language: String? = null): UserSetting
    suspend fun deleteUserSettings(id: UUID)
}
