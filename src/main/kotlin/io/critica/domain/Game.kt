package io.critica.domain

import io.critica.domain.events.DayEvent
import io.critica.domain.events.NightEvent
import io.critica.persistence.db.DayEvents
import io.critica.persistence.db.Games
import io.critica.persistence.db.NightEvents
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

class Game(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, Game>(Games)
    var date by Games.date
    var status by Games.status
    val players by Player optionalReferrersOn Players.gameId
    val dayEvents by DayEvent referrersOn DayEvents.game
    val nightEvents by NightEvent referrersOn NightEvents.game
    var winner by Games.winner

    fun getCurrentStage(): Any {
        return if (dayEvents.count() > nightEvents.count()) {
            dayEvents.last()
        } else nightEvents.last()
    }
}