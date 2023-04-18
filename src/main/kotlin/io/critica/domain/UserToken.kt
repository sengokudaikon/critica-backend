package io.critica.domain

import io.critica.persistence.db.UserTokens
import io.critica.persistence.db.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserToken(
    id: EntityID<Int>
): IntEntity(id) {
    companion object : IntEntityClass<UserToken>(UserTokens)
    var token by UserTokens.token
    var userId by User referencedOn Users.tokenId
    var createdAt by UserTokens.createdAt
    var expiresAt by UserTokens.expiresAt
}
