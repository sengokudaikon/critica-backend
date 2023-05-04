package net.critika.domain.events.model

import net.critika.application.vote.DayVoteResponse
import net.critika.domain.Player
import net.critika.persistence.db.DayVotes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class DayVote(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DayVote>(DayVotes)
    var day by DayEvent referencedOn DayVotes.day
    var voter by Player referencedOn DayVotes.player
    var target by Player referencedOn DayVotes.target

    fun toResponse(): DayVoteResponse {
        return DayVoteResponse(
            dayNumber = this.day.day,
            player = this.voter.toResponse(),
            target = this.target.toResponse(),
        )
    }
}
