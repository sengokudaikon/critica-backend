package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTokenTable : IntIdTable("user_tokens") {
    val userId = reference("userId", UserTable)
    val token = varchar("token", length = 200)
    val expiration = long("expiration")

    init {
        index(true, userId, token)
    }
}
