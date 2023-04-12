package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object NightEventTable : IntIdTable() {
    val game = reference("game_id", GameTable)
    val mafiaShot = reference("mafia_shot", PlayerTable).nullable()
    val detectiveCheck = reference("detective_check", PlayerTable).nullable()
    val donCheck = reference("don_check", PlayerTable).nullable()
}