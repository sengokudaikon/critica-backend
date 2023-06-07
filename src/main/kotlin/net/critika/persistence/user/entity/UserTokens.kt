package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UserTokens : KotlinxUUIDTable("user_tokens") {
    val userId = reference("user_id", Users)
    val token = varchar("token", length = 1000)
    val expiresAt = datetime("expires_at").default(LocalDateTime.now().plusDays(7))
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}