package net.critika.application.user.response

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.application.Response

@Serializable
data class RatingResponse(
    val userId: UUID,
    val totalPoints: Int,
    val bonusPoints: Int,
    val malusPoints: Int,
    val bestMovePoints: Int,
    val roleStatistics: List<RoleResponse>,
) : Response
