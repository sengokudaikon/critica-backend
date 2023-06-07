package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.user.response.RatingResponse
import net.critika.persistence.user.entity.RoleStatistics
import net.critika.persistence.user.entity.UserRatings
import org.jetbrains.exposed.dao.id.EntityID

class UserRating(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    fun toResponse(): RatingResponse {
        return RatingResponse(
            id = id.value.toString(),
            userId = userId.id.toString(),
            totalPoints = totalPoints,
            bonusPoints = bonusPoints,
            malusPoints = malusPoints,
            bestMovePoints = bestMovePoints,
            roleStatistics = roleStatistics.map { it.toResponse() },
        )
    }

    companion object : KotlinxUUIDEntityClass<UserRating>(UserRatings)

    var userId by User referencedOn UserRatings.userId
    var totalPoints by UserRatings.totalPoints
    var bonusPoints by UserRatings.bonusPoints
    var malusPoints by UserRatings.malusPoints
    var bestMovePoints by UserRatings.bestMovePoints
    val roleStatistics by RoleStatistic referrersOn RoleStatistics.userRatingId
    var createdAt by UserRatings.createdAt
    var updatedAt by UserRatings.updatedAt
}
