package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object UserRatings : UUIDTable("user_ratings") {
    val userId = reference("user_id", Users)
    val totalPoints = integer("total_points")
    val bonusPoints = integer("bonus_points")
    val malusPoints = integer("malus_points")
    val bestMovePoints = integer("best_move_points")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
