package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.user.response.RatingResponse
import net.critika.application.user.response.RoleResponse
import net.critika.persistence.user.entity.RoleStatistics
import net.critika.persistence.user.entity.UserRatings
import org.jetbrains.exposed.dao.id.EntityID

class UserRating(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    fun toResponse(): RatingResponse {
        return RatingResponse(
            userId = userId.id.value,
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

fun List<UserRating>.toTotal(id: UUID): RatingResponse {
    val allRoles = this.map { it.roleStatistics.toList() }.flatten()
    val roleStatsMap = allRoles.groupBy { it.role }.map { entry ->
        val stats = entry.value
        RoleResponse(
            role = entry.key.name,
            gamesWon = stats.sumOf { it.gamesWon },
            gamesTotal = stats.sumOf { it.gamesTotal },
            bonusPoints = stats.sumOf { it.bonusPoints },
        )
    }.toList()
    return RatingResponse(
        userId = id,
        totalPoints = this.sumOf { it.totalPoints },
        bonusPoints = this.sumOf { it.bonusPoints },
        malusPoints = this.sumOf { it.malusPoints },
        bestMovePoints = this.sumOf { it.bestMovePoints },
        roleStatistics = roleStatsMap,
    )
}
