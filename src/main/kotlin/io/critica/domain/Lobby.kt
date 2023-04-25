package io.critica.domain

import io.critica.application.lobby.response.LobbyResponse
import io.critica.persistence.db.Games
import io.critica.persistence.db.Lobbies
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Lobby(
    id: EntityID<UUID>,
): UUIDEntity(id) {

    companion object : UUIDEntityClass<Lobby>(Lobbies)
    var date by Lobbies.date
    var creator by User referencedOn Lobbies.creator
    var name by Lobbies.name
    val players by Player referrersOn Players.lobbyId
    val games by Game referrersOn Games.lobbyId

    fun toResponse(): LobbyResponse {
        return LobbyResponse(
            id = this.id.value.toString(),
            date = this.date,
            name = this.name,
            creator = this.creator.username,
        )
    }
}
