package net.critika.domain.gameprocess.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.player.response.PlayerResponse
import net.critika.persistence.gameprocess.entity.DayCandidates
import org.jetbrains.exposed.dao.id.EntityID

class DayCandidate(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<DayCandidate>(DayCandidates)
    var day by DayEvent referencedOn DayCandidates.day
    var player by Player referencedOn DayCandidates.player
    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.player.id.value,
            name = this.player.name,
            status = PlayerStatus.valueOf(this.player.status),
            inGame = this.player.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.player.bonusPoints,
            role = this.player.role?.let { PlayerRole.valueOf(it) },
            bestMove = this.player.bestMove,
            seat = this.player.seat,
        )
    }
}
