package net.critika.domain.user.model

import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.user.response.RoleResponse
import net.critika.persistence.user.entity.RoleStatistics
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.uuid.UUID

class RoleStatistic(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    fun toResponse(): RoleResponse {
        return RoleResponse(
            id = this.id.value.toString(),
            role = role.name,
            gamesWon = gamesWon,
            gamesTotal = gamesTotal,
            bonusPoints = bonusPoints,
        )
    }

    companion object : KotlinxUUIDEntityClass<RoleStatistic>(RoleStatistics)
    var userRatingId by UserRating referencedOn RoleStatistics.userRatingId
    var role by RoleStatistics.role
    var gamesWon by RoleStatistics.gamesWon
    var gamesTotal by RoleStatistics.gamesTotal
    var bonusPoints by RoleStatistics.bonusPoints
    var createdAt by RoleStatistics.createdAt
    var updatedAt by RoleStatistics.updatedAt
}
