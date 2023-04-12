package io.critica.domain.events

import io.critica.application.vote.DayVoteResponse
import io.critica.domain.Player
import io.critica.persistence.db.DayVotes
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DayVote(
    id: EntityID<Int>,
): IntEntity(id) {
    companion object : IntEntityClass<DayVote>(DayVotes)
    var day by DayEvent referencedOn DayVotes.day
    var player by Player referencedOn DayVotes.player
    var target by Player referencedOn DayVotes.target

    fun toResponse(): DayVoteResponse {
        return DayVoteResponse(
            dayNumber = this.day.day,
            player = this.player.toResponse(),
            target = this.target.toResponse(),
        )
    }
}