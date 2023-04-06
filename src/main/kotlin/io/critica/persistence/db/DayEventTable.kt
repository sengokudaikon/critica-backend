package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object DayEventTable : IntIdTable() {
    val game = reference("game_id", GameTable)
    val candidates = text("candidates").nullable()
    val votes = text("votes").nullable()
}