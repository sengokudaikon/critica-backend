package net.critika.domain.user.model

import net.critika.persistence.db.RoleStatistics
import net.critika.persistence.db.UserRatings
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserRating(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserRating>(UserRatings)

    var userId by User referencedOn UserRatings.userId
    var totalPoints by UserRatings.totalPoints
    var bonusPoints by UserRatings.bonusPoints
    var malusPoints by UserRatings.malusPoints
    var bestMovePoints by UserRatings.bestMovePoints
    val roleStatistics by RoleStatistic referrersOn RoleStatistics.userRatingId
}
