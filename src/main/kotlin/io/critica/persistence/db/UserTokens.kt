package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IdTable
import org.joda.time.DateTime
import java.util.*

object UserTokens : IdTable<UUID>("user_tokens") {
    override val id = uuid("id").entityId()
    val userId = reference("user_id", Users)
    val token = varchar("token", length = 1000)
    val expiresAt = long("expires_at").default(DateTime.now().plusDays(7).millis)
    val createdAt = long("created_at").default(DateTime.now().millis)
}
