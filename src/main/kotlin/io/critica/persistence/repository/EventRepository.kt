package io.critica.persistence.repository

import io.critica.domain.Game
import io.critica.domain.Player
import io.critica.domain.events.DayCandidate
import io.critica.domain.events.DayEvent
import io.critica.domain.events.DayVote
import io.critica.domain.events.NightEvent
import io.critica.persistence.db.DayEvents
import io.critica.persistence.db.Players
import io.critica.persistence.exception.PlayerException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class EventRepository {
    suspend fun startDay(game: Game, day: Int): DayEvent {
        return suspendedTransactionAsync {
            DayEvent.new {
                this.day = day
                this.game = game.id
            }
        }.await()
    }

    suspend fun startNight(game: Game, night: Int): NightEvent {
        return suspendedTransactionAsync {
            NightEvent.new {
                this.night = night
                this.game = game.id
            }
        }.await()
    }

    suspend fun addCandidate(game: Game, candidateId: Int): DayEvent {
        return suspendedTransactionAsync {
            val dayEvent = DayEvent.find { DayEvents.game eq game.id }.first()

            val candidateExists = dayEvent.candidates.any { it.player.id.value == candidateId }
            if (!candidateExists) {
                val player = Player.findById(candidateId) ?: throw PlayerException.NotFound("Player not found")
                dayEvent.candidates.plus(DayCandidate.new {
                    this.player = player
                    this.day = dayEvent
                })
            }
            dayEvent
        }.await()
    }

    suspend fun addNightEvent(game: Game): NightEvent {
        return suspendedTransactionAsync {
            NightEvent.new {
                this.game = game.id
            }
        }.await()
    }

    suspend fun addShot(game: Game, shot: Int): NightEvent {
        return suspendedTransactionAsync {
            val nightEvent = NightEvent.find { DayEvents.game eq game.id }.first()
            nightEvent.mafiaShot = shot.let { EntityID(it, Players) }
            nightEvent
        }.await()
    }

    suspend fun addCheck(game: Game, check: Int): NightEvent {
        return suspendedTransactionAsync {
            val nightEvent = NightEvent.find { DayEvents.game eq game.id }.first()
            nightEvent.detectiveCheck = check.let { EntityID(it, Players) }
            nightEvent
        }.await()
    }

    suspend fun addDonCheck(game: Game, check: Int): NightEvent {
        return suspendedTransactionAsync {
            val nightEvent = NightEvent.find { DayEvents.game eq game.id }.first()
            nightEvent.donCheck = check.let { EntityID(it, Players) }
            nightEvent
        }.await()
    }

    suspend fun addVote(game: Game, voter: Int, target: Int): DayEvent {
        return suspendedTransactionAsync {
            val dayEvent = DayEvent.find { DayEvents.game eq game.id }.first()
            dayEvent.votes.plus(DayVote.new {
                this.player = Player.findById(voter)?: throw PlayerException.NotFound("Player not found")
                this.day = dayEvent
                this.target = Player.findById(target)?: throw PlayerException.NotFound("Player not found")
            })
            dayEvent
        }.await()
    }
}
