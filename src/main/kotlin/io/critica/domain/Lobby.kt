package io.critica.domain

import io.critica.persistence.db.Lobbies
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Lobby(
    id: EntityID<Int>,
): IntEntity(id) {

    companion object : IntEntityClass<Lobby>(Lobbies)
    var date by Lobbies.date
//    var creator by User referencedOn Lobbies.creator
    var name by Lobbies.name
//    val players by Player referrersOn Players.lobbyId
//    val games by Game referrersOn Games.lobbyId
}
