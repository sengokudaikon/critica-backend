package io.critica.domain.events

import io.critica.domain.Player
import io.critica.persistence.db.DayCandidates
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DayCandidate(
    id: EntityID<Int>
): IntEntity(id) {
    companion object : IntEntityClass<DayCandidate>(DayCandidates)
    var day by DayEvent referencedOn DayCandidates.day
    var player by Player referencedOn DayCandidates.player
}
