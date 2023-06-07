package net.critika.domain.gameprocess.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.player.response.PlayerResponse
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.Lobby
import net.critika.domain.user.model.User
import net.critika.persistence.gameprocess.entity.Players
import org.jetbrains.exposed.dao.id.EntityID

class Player(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Player>(Players)
    var user by User optionalReferencedOn Players.userId
    var game by Game optionalReferencedOn Players.gameId
    var lobby by Lobby optionalReferencedOn Players.lobbyId
    var name by Players.name
    var status by Players.status
    var role by Players.role
    var bestMove by Players.bestMove
    var bonusPoints by Players.bonusPoints
    var foulPoints by Players.foulPoints
    var seat by Players.seat
    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.id.value,
            name = this.name,
            status = PlayerStatus.valueOf(this.status),
            inGame = this.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.bonusPoints,
            bestMove = this.bestMove,
            role = this.role?.let { PlayerRole.valueOf(it) },
            seat = this.seat,
        )
    }
}
