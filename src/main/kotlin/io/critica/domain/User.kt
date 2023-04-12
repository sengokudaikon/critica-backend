package io.critica.domain

import io.critica.persistence.db.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(
    id: EntityID<Int>,

): IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.playerName
    var email by Users.email
    var password by Users.hashedPassword
    var isAdmin by Users.isAdmin
}