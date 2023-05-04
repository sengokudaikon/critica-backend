package net.critika.domain

import net.critika.application.lobby.response.LobbyResponse
import net.critika.domain.user.model.User
import net.critika.persistence.db.Games
import net.critika.persistence.db.Lobbies
import net.critika.persistence.db.Players
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Lobby(
    id: EntityID<UUID>,
) : UUIDEntity(id) {

    companion object : UUIDEntityClass<Lobby>(Lobbies)
    var date by Lobbies.date
    var creator by User referencedOn Lobbies.creator
    val players by Player optionalReferrersOn Players.lobbyId
    val games by Game referrersOn Games.lobbyId

    fun toResponse(): LobbyResponse {
        return LobbyResponse(
            id = this.id.value.toString(),
            date = this.date.toString(),
            creator = this.creator.username,
            games = this.games.map { it.toResponse() },
        )
    }
}
