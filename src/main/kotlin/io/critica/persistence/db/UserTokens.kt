package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object UserTokens : IdTable<UUID>("user_tokens") {
    override val id = uuid("id").entityId()
    val userId = reference("userId", Users)
    val token = varchar("token", length = 200)
    val expiresAt = long("expiresAt")
    val createdAt = long("createdAt")

    init {
        index(true, userId, token)
    }
}
