package net.critika.domain.club.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.lobby.response.LobbyResponse
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.persistence.club.entity.Games
import net.critika.persistence.club.entity.Lobbies
import net.critika.persistence.gameprocess.entity.Players
import org.jetbrains.exposed.dao.id.EntityID

class Lobby(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {

    companion object : KotlinxUUIDEntityClass<Lobby>(Lobbies)
    var date by Lobbies.date
    var creator by User referencedOn Lobbies.creator
    val players by Player optionalReferrersOn Players.lobbyId
    val games by Game referrersOn Games.lobbyId

    fun toResponse(): LobbyResponse {
        return LobbyResponse(
            id = this.id.value.toString(),
            date = this.date.toString(),
            creator = this.creator.playerName,
            games = this.games.map { it.toResponse() },
        )
    }
}
