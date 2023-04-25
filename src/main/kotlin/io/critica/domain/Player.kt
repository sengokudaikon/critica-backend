package io.critica.domain

import io.critica.application.player.PlayerResponse
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Player(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Player>(Players)
    var user by User optionalReferencedOn Players.userId
    var game by Game optionalReferencedOn Players.gameId
    var lobby by Lobby referencedOn Players.lobbyId
    var name by Players.name
    var status by Players.status
    var role by Players.role
    var bonusPoints by Players.bonusPoints
    var seat by Players.seat
    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.id.value,
            name = this.name,
            status = PlayerStatus.valueOf(this.status),
            inGame = this.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.bonusPoints,
            role = PlayerRole.valueOf(this.role),
            seat = this.seat
        )
    }
}
