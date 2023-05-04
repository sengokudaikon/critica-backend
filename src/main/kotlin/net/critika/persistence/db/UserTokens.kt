package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.joda.time.DateTime

object UserTokens : UUIDTable("user_tokens") {
    val userId = reference("user_id", Users)
    val token = varchar("token", length = 1000)
    val expiresAt = long("expires_at").default(DateTime.now().plusDays(7).millis)
    val createdAt = long("created_at").default(DateTime.now().millis)
}
