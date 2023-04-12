package io.critica.domain.events

import io.critica.persistence.db.NightEvents
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
class NightEvent(
    id: EntityID<Int>,
): IntEntity(id), Event {
    companion object : IntEntityClass<NightEvent>(NightEvents)
    var night by NightEvents.night
    var game by NightEvents.game
    var mafiaShot by NightEvents.mafiaShot
    var detectiveCheck by NightEvents.detectiveCheck
    var donCheck by NightEvents.donCheck
}