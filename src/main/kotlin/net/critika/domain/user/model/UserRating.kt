package net.critika.domain.user.model

import net.critika.application.user.response.RatingResponse
import net.critika.persistence.db.RoleStatistics
import net.critika.persistence.db.UserRatings
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserRating(id: EntityID<UUID>) : UUIDEntity(id) {
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

    companion object : UUIDEntityClass<UserRating>(UserRatings)

    var userId by User referencedOn UserRatings.userId
    var totalPoints by UserRatings.totalPoints
    var bonusPoints by UserRatings.bonusPoints
    var malusPoints by UserRatings.malusPoints
    var bestMovePoints by UserRatings.bestMovePoints
    val roleStatistics by RoleStatistic referrersOn RoleStatistics.userRatingId
}
