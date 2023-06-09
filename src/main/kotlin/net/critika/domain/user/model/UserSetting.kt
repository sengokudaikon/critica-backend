package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.user.response.UserSettingsResponse
import net.critika.persistence.user.entity.UserSettings
import org.jetbrains.exposed.dao.id.EntityID

class UserSetting(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<UserSetting>(UserSettings)

    var userId by User referencedOn UserSettings.userId
    var emailVerified by UserSettings.emailVerified
    var publicVisibility by UserSettings.publicVisibility
    var pushNotifications by UserSettings.pushNotifications
    var language by UserSettings.language
    var promotion by UserSettings.promotion
    var createdAt by UserSettings.createdAt
    var updatedAt by UserSettings.updatedAt

    fun toResponse(): UserSettingsResponse {
        return UserSettingsResponse(
            id = this.id.toString(),
            publicVisibility = this.publicVisibility,
            pushNotificationsEnabled = this.pushNotifications,
            language = language.name,
            promoted = promotion,
            emailConfirmed = emailVerified,
        )
    }
}
