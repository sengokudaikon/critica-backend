package net.critika.persistence.gameprocess.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.persistence.club.entity.Games

object NightEvents : KotlinxUUIDTable("night_events") {
    val night = integer("night")
    val game = reference("game_id", Games)
    val mafiaShot = reference("mafia_shot", Players).nullable()
    val detectiveCheck = reference("detective_check", Players).nullable()
    val donCheck = reference("don_check", Players).nullable()
}
