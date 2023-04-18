package io.critica.domain

import io.critica.application.game.GameResponse
import io.critica.domain.events.DayEvent
import io.critica.domain.events.Event
import io.critica.domain.events.NightEvent
import io.critica.persistence.db.DayEvents
import io.critica.persistence.db.Games
import io.critica.persistence.db.NightEvents
import io.critica.persistence.db.Players
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

class Game(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, Game>(Games)
    var date by Games.date
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
            id = this.id.value,
            date = this.date,
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
