package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object DayEvents : IntIdTable() {
    val game = reference("game_id", Games)
    val day = integer("day")
}
