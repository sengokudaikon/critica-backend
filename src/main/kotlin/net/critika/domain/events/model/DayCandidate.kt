package net.critika.domain.events.model

import net.critika.application.player.response.PlayerResponse
import net.critika.domain.Player
import net.critika.domain.PlayerRole
import net.critika.domain.PlayerStatus
import net.critika.persistence.db.DayCandidates
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class DayCandidate(
    id: EntityID<UUID>
): UUIDEntity(id) {
    companion object : UUIDEntityClass<DayCandidate>(DayCandidates)
    var day by DayEvent referencedOn DayCandidates.day
    var player by Player referencedOn DayCandidates.player
    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.player.id.value,
            name = this.player.name,
            status = PlayerStatus.valueOf(this.player.status),
            inGame = this.player.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.player.bonusPoints,
            role = this.player.role?.let { PlayerRole.valueOf(it)},
            bestMove = this.player.bestMove,
            seat = this.player.seat
        )
    }
}

