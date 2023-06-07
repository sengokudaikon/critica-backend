package net.critika.persistence.gameprocess.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.gameprocess.model.DayStage
import net.critika.persistence.club.entity.Games

object DayEvents : KotlinxUUIDTable("day_events") {
    val game = reference("game_id", Games)
    val day = integer("day")
    val stage = enumeration("stage", DayStage::class)
}