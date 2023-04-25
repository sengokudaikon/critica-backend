package io.critica.domain

import io.critica.persistence.db.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class User(
    id: EntityID<UUID>,

): UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.playerName
    var email by Users.email
    var password by Users.hashedPassword
    var role by Users.role
}
