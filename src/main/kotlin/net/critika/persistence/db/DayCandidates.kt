package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable

object DayCandidates : UUIDTable("day_candidates") {
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
}
