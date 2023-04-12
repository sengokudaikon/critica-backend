package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object NightEvents : IntIdTable() {
    val night = integer("night")
    val game = reference("game_id", Games)
    val mafiaShot = reference("mafia_shot", Players).nullable()
    val detectiveCheck = reference("detective_check", Players).nullable()
    val donCheck = reference("don_check", Players).nullable()
}
