package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.persistence.club.entity.Clubs
import org.jetbrains.exposed.sql.javatime.datetime

object UserRatings : KotlinxUUIDTable("user_ratings") {
    val clubId = reference("club_id", Clubs)
    val userId = reference("user_id", Users)
    val totalPoints = integer("total_points")
    val bonusPoints = integer("bonus_points")
    val malusPoints = integer("malus_points")
    val bestMovePoints = integer("best_move_points")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}