package net.critika.domain.events.model

import net.critika.application.stage.NightStageResponse
import net.critika.domain.Player
import net.critika.domain.events.Event
import net.critika.persistence.db.NightEvents
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class NightEvent(
    id: EntityID<UUID>,
): UUIDEntity(id), Event {
    companion object : UUIDEntityClass<NightEvent>(NightEvents)
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
