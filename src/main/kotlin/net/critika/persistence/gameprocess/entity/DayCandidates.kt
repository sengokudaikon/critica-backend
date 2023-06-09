package net.critika.persistence.gameprocess.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable

object DayCandidates : KotlinxUUIDTable("day_candidates") {
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
}
