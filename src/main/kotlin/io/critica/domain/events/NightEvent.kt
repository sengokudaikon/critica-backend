package io.critica.domain.events

import io.critica.application.stage.NightStageResponse
import io.critica.domain.Player
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

    fun toResponse(): NightStageResponse {
        return NightStageResponse(
            dayNumber = this.night,
            mafiaShot = this.mafiaShot?.let { Player[it].toResponse() },
            detectiveCheck = this.detectiveCheck?.let { Player[it].toResponse() },
            donCheck = this.donCheck?.let { Player[it].toResponse() }
        )
    }
}
