package io.critica.domain

import io.critica.persistence.db.UserTokens
import io.critica.persistence.db.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserToken(
    id: EntityID<UUID>
): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserToken>(UserTokens)
    var token by UserTokens.token
    var userId by User referencedOn Users.tokenId
    var createdAt by UserTokens.createdAt
    var expiresAt by UserTokens.expiresAt
}
