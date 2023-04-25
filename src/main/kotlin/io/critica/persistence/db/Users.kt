package io.critica.persistence.db

import io.critica.domain.UserRole
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object Users : IdTable<UUID>(name = "users") {
    override val id = uuid("id").entityId()
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val playerName = varchar("player_name", 255)
    val role = enumeration("role", UserRole::class)
    val tokenId = reference("token_id", UserTokens)
}
