package net.critika.domain.events.model

import net.critika.application.stage.DayStageResponse
import net.critika.domain.events.Event
import net.critika.persistence.db.DayCandidates
import net.critika.persistence.db.DayEvents
import net.critika.persistence.db.DayVotes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class DayEvent(
    id: EntityID<UUID>,
): UUIDEntity(id), Event {
    companion object : UUIDEntityClass<DayEvent>(DayEvents)
    var game by DayEvents.game
    var day by DayEvents.day
    var stage by DayEvents.stage
    val candidates by DayCandidate referrersOn DayCandidates.day
    val votes by DayVote referrersOn DayVotes.day

    fun toResponse(): DayStageResponse
    {
        return DayStageResponse(
            dayNumber = this.day,
            candidates = this.candidates.map { it.toResponse() },
            votes = this.votes.map { it.toResponse() }
        )
    }
}
