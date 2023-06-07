package net.critika.domain.gameprocess.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.stage.response.NightStageResponse
import net.critika.domain.gameprocess.Event
import net.critika.persistence.gameprocess.entity.NightEvents
import org.jetbrains.exposed.dao.id.EntityID

class NightEvent(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id), Event {
    companion object : KotlinxUUIDEntityClass<NightEvent>(NightEvents)
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
            donCheck = this.donCheck?.let { Player[it].toResponse() },
        )
    }
}
