package net.critika.persistence.db

import net.critika.domain.PlayerRole
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object RoleStatistics : UUIDTable("user_role_statistics") {
    val userRatingId = reference("userRatingId", UserRatings)
    val role = enumeration("role", PlayerRole::class)
    val gamesWon = integer("gamesWon")
    val gamesTotal = integer("gamesTotal")
    val bonusPoints = integer("bonusPoints")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")
}
