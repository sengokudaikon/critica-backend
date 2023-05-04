package net.critika.domain.user.model

import net.critika.persistence.db.UserSettings
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserSetting(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserSetting>(UserSettings)

    var userId by User referencedOn UserSettings.userId
    var emailVerified by UserSettings.emailVerified
    var publicVisibility by UserSettings.publicVisibility
    var pushNotifications by UserSettings.pushNotifications
    var language by UserSettings.language
    var promotion by UserSettings.promotion
    var createdAt by UserSettings.createdAt
    var updatedAt by UserSettings.updatedAt
}
