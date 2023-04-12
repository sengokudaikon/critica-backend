package io.critica.domain

import io.critica.application.player.PlayerResponse
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    var user by User optionalReferencedOn Players.userId
    var game by Game optionalReferencedOn Players.gameId
    var lobby by Lobby referencedOn Players.lobbyId
    var name by Players.name
    var status by Players.status
    var role by Players.role
    var bonusPoints by Players.bonusPoints

    fun toResponse(): PlayerResponse {
        return PlayerResponse(
            id = this.id.value,
            name = this.name,
            alive = this.status == PlayerStatus.ALIVE.toString(),
            inGame = this.status == PlayerStatus.INGAME.toString(),
            bonusPoints = this.bonusPoints,
        )
    }
}
