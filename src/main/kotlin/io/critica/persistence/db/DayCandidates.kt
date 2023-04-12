package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object DayCandidates: IntIdTable() {
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
}
