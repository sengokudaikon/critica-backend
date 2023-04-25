package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object DayCandidates: IdTable<UUID>("day_candidates") {
    override val id = uuid("id").entityId()
    val day = reference("day_id", DayEvents)
    val player = reference("player_id", Players)
}
