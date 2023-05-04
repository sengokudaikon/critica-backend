package net.critika.domain.user.model

import net.critika.persistence.db.RoleStatistics
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class RoleStatistic(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleStatistic>(RoleStatistics)
    var userRatingId by UserRating referencedOn RoleStatistics.userRatingId
    var role by RoleStatistics.role
    var gamesWon by RoleStatistics.gamesWon
    var gamesTotal by RoleStatistics.gamesTotal
    var bonusPoints by RoleStatistics.bonusPoints
}