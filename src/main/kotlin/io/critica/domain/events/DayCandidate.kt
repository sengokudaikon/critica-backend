package io.critica.domain.events

import io.critica.application.player.PlayerResponse
import io.critica.domain.Player
import io.critica.domain.PlayerStatus
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
    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.player.id.value,
            name = this.player.name,
            alive = this.player.status == PlayerStatus.ALIVE.toString(),
            inGame = this.player.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.player.bonusPoints,
        )
    }
}

