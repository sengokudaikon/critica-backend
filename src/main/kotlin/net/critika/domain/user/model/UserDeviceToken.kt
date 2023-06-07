package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.persistence.user.entity.UserDeviceTokens
import org.jetbrains.exposed.dao.id.EntityID

class UserDeviceToken(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<UserDeviceToken>(UserDeviceTokens)
    var token by UserDeviceTokens.token
    var userId by User referencedOn UserDeviceTokens.userId
    var createdAt by UserDeviceTokens.createdAt
    var expiresAt by UserDeviceTokens.expiresAt
}
