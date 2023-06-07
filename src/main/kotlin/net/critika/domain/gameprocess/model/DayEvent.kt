package net.critika.domain.gameprocess.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.stage.response.DayStageResponse
import net.critika.domain.gameprocess.Event
import net.critika.persistence.gameprocess.entity.DayCandidates
import net.critika.persistence.gameprocess.entity.DayEvents
import net.critika.persistence.gameprocess.entity.DayVotes
import org.jetbrains.exposed.dao.id.EntityID

class DayEvent(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id), Event {
    companion object : KotlinxUUIDEntityClass<DayEvent>(DayEvents)
    var game by DayEvents.game
    var day by DayEvents.day
    var stage by DayEvents.stage
    val candidates by DayCandidate referrersOn DayCandidates.day
    val votes by DayVote referrersOn DayVotes.day

    fun toResponse(): DayStageResponse {
        return DayStageResponse(
            stage = this.stage,
            dayNumber = this.day,
            candidates = this.candidates.map { it.toResponse() },
            votes = this.votes.map { it.toResponse() },
        )
    }
}
