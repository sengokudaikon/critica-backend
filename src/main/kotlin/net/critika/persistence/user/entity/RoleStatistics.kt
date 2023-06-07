package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.gameprocess.model.PlayerRole
import org.jetbrains.exposed.sql.javatime.datetime

object RoleStatistics : KotlinxUUIDTable("user_role_statistics") {
    val userRatingId = reference("userRatingId", UserRatings)
    val role = enumeration("role", PlayerRole::class)
    val gamesWon = integer("gamesWon")
    val gamesTotal = integer("gamesTotal")
    val bonusPoints = integer("bonusPoints")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")
}