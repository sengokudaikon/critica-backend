package net.critika.persistence.gameprocess.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable

object DayVotes : KotlinxUUIDTable("day_event_votes") {
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
    val target = reference("candidate_id", Players)
}