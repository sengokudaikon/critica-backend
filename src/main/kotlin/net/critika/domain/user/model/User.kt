package net.critika.domain.user.model

import net.critika.domain.Player
import net.critika.persistence.db.UserSettings
import net.critika.persistence.db.UserTokens
import net.critika.persistence.db.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

class User(
    id: EntityID<UUID>,

): UUIDEntity(id) {
    fun toPlayer(): Player? {
        return Player.findById(this.id)
    }

    companion object : UUIDEntityClass<User>(Users) {
        fun fromResultRow(row: ResultRow): User {
            return User(row[Users.id]).apply {
                username = row[Users.playerName]
                email = row[Users.email]
                password = row[Users.hashedPassword]
                role = row[Users.role]
            }
        }
    }

    var username by Users.playerName
    var email by Users.email
    var password by Users.hashedPassword
    var role by Users.role
    val tokens by UserToken referrersOn UserTokens.userId
    val settings by UserSetting referrersOn UserSettings.userId
}
