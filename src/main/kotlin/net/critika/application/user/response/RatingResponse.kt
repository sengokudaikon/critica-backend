package net.critika.application.user.response

import kotlinx.serialization.Serializable

@Serializable
data class RatingResponse(
    val id: String,
    val userId: String,
    val totalPoints: Int,
    val bonusPoints: Int,
    val malusPoints: Int,
    val bestMovePoints: Int,
    val roleStatistics: List<RoleResponse>,
)
