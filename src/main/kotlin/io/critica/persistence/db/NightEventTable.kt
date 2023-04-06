package io.critica.persistence.db

import io.critica.domain.NightEvent
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object NightEventTable : IntIdTable() {
    val game = reference("game_id", GameTable)
    val mafiaShot = reference("mafia_shot", PlayerTable).nullable()
    val detectiveCheck = reference("detective_check", PlayerTable).nullable()
    val donCheck = reference("don_check", PlayerTable).nullable()

    fun toNightEvent(it: ResultRow): NightEvent {
        val id = it[NightEventTable.id].value
        val gameId = it[game].value
        val mafiaShot = it[NightEventTable.mafiaShot]
        val detectiveCheck = it[NightEventTable.detectiveCheck]
        val donCheck = it[NightEventTable.donCheck]

        return NightEvent(id, gameId, mafiaShot?.value, detectiveCheck?.value, donCheck?.value)
    }
}