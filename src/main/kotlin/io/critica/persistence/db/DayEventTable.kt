package io.critica.persistence.db

import io.critica.domain.DayEvent
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object DayEventTable : IntIdTable() {
    val game = reference("game_id", GameTable)
    val candidates = text("candidates").nullable()
    val votes = text("votes").nullable()

    fun toDayEvent(it: ResultRow): DayEvent {
        val id = it[DayEventTable.id].value
        val gameId = it[game].value
        val candidates = it[DayEventTable.candidates]?.split(",")?.map { it.toInt() }!!.toList()
        val votes = it[DayEventTable.votes]?.split(",")?.map { it.toInt() }!!.toList()

        return DayEvent(id, gameId, candidates, votes)
    }
}