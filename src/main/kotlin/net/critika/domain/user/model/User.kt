package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.user.response.UserResponse
import net.critika.domain.gameprocess.model.Player
import net.critika.persistence.user.entity.UserSettings
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.dao.id.EntityID

class User(
    id: EntityID<UUID>,

) : KotlinxUUIDEntity(id) {
    fun toPlayer(): Player? {
        return Player.findById(this.id)
    }

    fun toResponse(): UserResponse {
        return UserResponse(
            id = this.id.value.toString(),
            playerName = this.playerName,
            email = this.email,
        )
    }

    companion object : KotlinxUUIDEntityClass<User>(Users)

    var uid by Users.uid
    var clubId by Users.clubId
    var playerName by Users.playerName
    var email by Users.email
    var role by Users.role
    val settings by UserSetting referrersOn UserSettings.userId
    var created by Users.createdAt
    var updated by Users.updatedAt
}
