package io.critica.domain

import io.critica.application.lobby.response.LobbyResponse
import io.critica.persistence.db.Games
import io.critica.persistence.db.Lobbies
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Lobby(
    id: EntityID<Int>,
): IntEntity(id) {

    companion object : IntEntityClass<Lobby>(Lobbies)
    var date by Lobbies.date
    var creator by User referencedOn Lobbies.creator
    var name by Lobbies.name
    val players by Player referrersOn Players.lobbyId
    val games by Game referrersOn Games.lobbyId

    fun toResponse(): LobbyResponse {
        return LobbyResponse(
            id = this.id.value,
            date = this.date,
            name = this.name,
        )
    }
}
