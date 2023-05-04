package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable

object DayVotes : UUIDTable("day_event_votes") {
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
    val target = reference("candidate_id", Players)
}
