package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.persistence.user.entity.UserTokens
import org.jetbrains.exposed.dao.id.EntityID

class UserToken(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<UserToken>(UserTokens)
    var token by UserTokens.token
    var userId by User referencedOn UserTokens.userId
    var createdAt by UserTokens.createdAt
    var expiresAt by UserTokens.expiresAt
}
