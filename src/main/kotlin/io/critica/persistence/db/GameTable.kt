package io.critica.persistence.db

import io.critica.domain.GameStatus
import org.jetbrains.exposed.dao.id.IntIdTable

object GameTable : IntIdTable() {
    val date = varchar("date", 255)
    val status = enumeration("status", GameStatus::class)
    val dayEvents = reference("day_events", DayEventTable)
    val nightEvents = reference("night_events", NightEventTable)
    val winner = varchar("winner", 255).nullable()
}