package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTokens : IntIdTable("user_tokens") {
    val userId = reference("userId", Users)
    val token = varchar("token", length = 200)
    val expiresAt = long("expiresAt")
    val createdAt = long("createdAt")

    init {
        index(true, userId, token)
    }
}
