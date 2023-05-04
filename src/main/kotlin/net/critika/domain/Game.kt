package net.critika.domain

import net.critika.application.game.response.GameResponse
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.Event
import net.critika.domain.events.model.NightEvent
import net.critika.domain.user.model.User
import net.critika.persistence.db.DayEvents
import net.critika.persistence.db.Games
import net.critika.persistence.db.NightEvents
import net.critika.persistence.db.Players
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Game(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Game>(Games)
    var date by Games.date
    var host by User optionalReferencedOn Games.hostId
    var lobby by Lobby referencedOn Games.lobbyId
    var status by Games.status
    val players by Player optionalReferrersOn Players.gameId
    val dayEvents by DayEvent referrersOn DayEvents.game
    val nightEvents by NightEvent referrersOn NightEvents.game
    val playersEliminated by Player optionalReferrersOn Players.gameId
    var winner by Games.winner

    fun getCurrentStage(): Event {
        return if (dayEvents.count() > nightEvents.count()) {
            dayEvents.last()
        } else nightEvents.last()
    }

    fun toResponse(): GameResponse {
        val stage = this.getCurrentStage()
        val playersEliminated = if (!this.playersEliminated.empty()) {
            this.playersEliminated.map { it.toResponse()}
        } else { listOf() }

        return GameResponse(
            id = this.id.value.toString(),
            date = this.date.toString(),
            host = this.host?.toPlayer()?.toResponse(),
            currentStage = when (stage) {
                is DayEvent -> stage.toResponse()
                is NightEvent -> stage.toResponse()
                else -> { null }
            },
            players = this.players.map { it.toResponse() },
            nominates = when (stage) {
                is DayEvent -> stage.candidates.map { it.toResponse() }
                else -> listOf()
            },
            votes = when (stage) {
                is DayEvent -> stage.votes.map { it.toResponse() }
                else -> listOf()
            },
            mafiaShot = when (stage) {
                is NightEvent -> stage.mafiaShot?.let { Player[it].toResponse() }
                else -> null
            },
            detectiveCheck = when (stage) {
                is NightEvent -> stage.detectiveCheck?.let { Player[it].toResponse() }
                else -> null
            },
            donCheck = when (stage) {
                is NightEvent -> stage.donCheck?.let { Player[it].toResponse() }
                else -> null
            },
            playersEliminated = playersEliminated,
        )
    }
}
