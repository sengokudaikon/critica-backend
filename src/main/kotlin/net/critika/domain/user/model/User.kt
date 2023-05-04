package net.critika.domain.user.model

import net.critika.application.user.response.UserResponse
import net.critika.domain.Player
import net.critika.persistence.db.UserSettings
import net.critika.persistence.db.UserTokens
import net.critika.persistence.db.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class User(
    id: EntityID<UUID>,

) : UUIDEntity(id) {
    fun toPlayer(): Player? {
        return Player.findById(this.id)
    }

    fun toResponse(): UserResponse {
        return UserResponse(
            id = this.id.value.toString(),
            name = this.username,
            email = this.email,
        )
    }

    companion object : UUIDEntityClass<User>(Users)

    var username by Users.playerName
    var email by Users.email
    var password by Users.hashedPassword
    var role by Users.role
    val tokens by UserToken referrersOn UserTokens.userId
    val settings by UserSetting referrersOn UserSettings.userId
}
