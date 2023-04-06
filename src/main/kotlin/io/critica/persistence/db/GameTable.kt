package io.critica.persistence.db

import io.critica.domain.GameStatus
import org.jetbrains.exposed.dao.id.IntIdTable

object GameTable : IntIdTable() {
    val date = varchar("date", 255)
    val status = enumeration("status", GameStatus::class)
    val dayEvents = text("day_events")
    val nightEvents = text("night_events")
    val winner = varchar("winner", 255).nullable()
}