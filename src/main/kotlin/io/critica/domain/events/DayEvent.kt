package io.critica.domain.events

import io.critica.application.stage.DayStageResponse
import io.critica.persistence.db.DayCandidates
import io.critica.persistence.db.DayEvents
import io.critica.persistence.db.DayVotes
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
class DayEvent(
    id: EntityID<Int>,
): IntEntity(id), Event {
    companion object : IntEntityClass<DayEvent>(DayEvents)
    var game by DayEvents.game
    var day by DayEvents.day
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