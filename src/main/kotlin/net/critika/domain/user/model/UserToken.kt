package net.critika.domain.user.model

import net.critika.persistence.db.UserTokens
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserToken(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserToken>(UserTokens)
    var token by UserTokens.token
    var userId by User referencedOn UserTokens.userId
    var createdAt by UserTokens.createdAt
    var expiresAt by UserTokens.expiresAt
}