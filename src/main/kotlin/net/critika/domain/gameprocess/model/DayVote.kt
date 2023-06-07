package net.critika.domain.gameprocess.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.vote.DayVoteResponse
import net.critika.persistence.gameprocess.entity.DayVotes
import org.jetbrains.exposed.dao.id.EntityID

class DayVote(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<DayVote>(DayVotes)
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
