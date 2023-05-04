package net.critika.persistence.db

import net.critika.domain.events.model.DayStage
import org.jetbrains.exposed.dao.id.UUIDTable

object DayEvents : UUIDTable("day_events") {
    val game = reference("game_id", Games)
    val day = integer("day")
    val stage = enumeration("stage", DayStage::class)
}
