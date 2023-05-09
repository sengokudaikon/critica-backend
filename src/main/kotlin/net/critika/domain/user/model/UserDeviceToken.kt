package net.critika.domain.user.model

import net.critika.persistence.db.UserDeviceTokens
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserDeviceToken(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDeviceToken>(UserDeviceTokens)
    var token by UserDeviceTokens.token
    var userId by User referencedOn UserDeviceTokens.userId
    var createdAt by UserDeviceTokens.createdAt
    var expiresAt by UserDeviceTokens.expiresAt
}
