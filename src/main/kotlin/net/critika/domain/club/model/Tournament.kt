package net.critika.domain.club.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.tournament.response.TournamentResponse
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.persistence.club.entity.Games
import net.critika.persistence.club.entity.Lobbies
import net.critika.persistence.club.entity.Tournaments
import net.critika.persistence.gameprocess.entity.Players
import org.jetbrains.exposed.dao.id.EntityID

class Tournament(
    id: EntityID<UUID>,
) : KotlinxUUIDEntity(id) {
    fun toResponse(): TournamentResponse {
        return TournamentResponse(
            id = id.value,
            name = name,
            clubId = club?.id?.value,
            date = date.toString(),
            playerLimit = playerLimit,
            players = players.map { it.toResponse() },
            creator = creator.toResponse(),
            description = description,
            location = location,
            isFinished = isFinished,
            games = games.map { it.toResponse() },
            lobbies = lobbies.map { it.toResponse() },
        )
    }

    companion object : KotlinxUUIDEntityClass<Tournament>(Tournaments)
    var name by Tournaments.name
    var club by Club optionalReferencedOn Tournaments.clubId
    var date by Tournaments.date
    var createdAt by Tournaments.createdAt
    var updatedAt by Tournaments.updatedAt
    var playerLimit by Tournaments.playerLimit
    val players by Player optionalReferrersOn Players.tournamentId
    var creator by User referencedOn Tournaments.creator
    var location by Tournaments.location
    var description by Tournaments.description
    var isFinished by Tournaments.isFinished
    val games by Game optionalReferrersOn Games.tournamentId
    val lobbies by Lobby optionalReferrersOn Lobbies.tournamentId
}
