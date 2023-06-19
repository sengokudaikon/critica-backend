package net.critika.domain.user.repository

import kotlinx.uuid.UUID
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserSetting

interface UserSettingsRepositoryPort {
    suspend fun updatePromotion(userId: UUID, b: Boolean?)
    suspend fun findUsersRequestingPromotion(): List<User>
    suspend fun updatePublicVisibility(userId: UUID, b: Boolean)
    suspend fun updatePushNotifications(userId: UUID, b: Boolean)
    suspend fun updateLanguage(userId: UUID, language: String)
    suspend fun getUserSettingsByUserId(userId: UUID): UserSetting
    suspend fun createUserSettings(userId: UUID, language: String? = null): UserSetting
    suspend fun deleteUserSettings(id: UUID)
}
