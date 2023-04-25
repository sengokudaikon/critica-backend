package io.critica.domain.events

import io.critica.application.player.PlayerResponse
import io.critica.domain.Player
import io.critica.domain.PlayerRole
import io.critica.domain.PlayerStatus
import io.critica.persistence.db.DayCandidates
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
            role = PlayerRole.valueOf(this.player.role),
            seat = this.player.seat
        )
    }
}

