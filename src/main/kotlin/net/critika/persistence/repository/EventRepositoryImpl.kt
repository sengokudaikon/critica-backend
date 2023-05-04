package net.critika.persistence.repository

import net.critika.application.game.response.GameResponse
import net.critika.domain.Game
import net.critika.domain.Player
import net.critika.domain.PlayerStatus
import net.critika.domain.events.model.DayCandidate
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.model.DayStage
import net.critika.domain.events.model.DayVote
import net.critika.domain.events.Event
import net.critika.domain.events.model.NightEvent
import net.critika.domain.events.repository.EventRepository
import net.critika.persistence.db.Players
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class EventRepositoryImpl: EventRepository {
    override suspend fun startDay(game: Game, day: Int): GameResponse {
        return suspendedTransactionAsync {
            DayEvent.new {
                this.day = day
                this.game = game.id
                this.stage = DayStage.DISCUSS
            }.toGameResponse(game, game.players.map { it.toResponse() })
        }.await()
    }

    override suspend fun startNight(game: Game, night: Int): GameResponse {
        return suspendedTransactionAsync {
            NightEvent.new {
                this.night = night
                this.game = game.id
            }.toGameResponse(game, game.players.map { it.toResponse() })
        }.await()
    }

    override suspend fun addCandidate(dayEvent: DayEvent, candidate: Int): DayEvent {
        return suspendedTransactionAsync {
            val candidateExists = dayEvent.candidates.any { it.player.seat == candidate }
            if (!candidateExists) {
                val player = Player.find { Players.seat eq candidate }.first()
                dayEvent.candidates.plus(DayCandidate.new {
                    this.player = player
                    this.day = dayEvent
                })
            }
            dayEvent
        }.await()
    }

    override suspend fun addShot(nightEvent: NightEvent, shot: Int): NightEvent {
        return suspendedTransactionAsync {
            val player = Player.find { Players.seat eq shot }.first()
            player.status = PlayerStatus.DEAD.toString()
            nightEvent.mafiaShot = player.id
            nightEvent
        }.await()
    }

    override suspend fun addCheck(nightEvent: NightEvent, check: Int): NightEvent {
        return suspendedTransactionAsync {
            val player = Player.find { Players.seat eq check }.first()
            nightEvent.detectiveCheck = player.id
            nightEvent
        }.await()
    }

    override suspend fun addDonCheck(nightEvent: NightEvent, check: Int): NightEvent {
        return suspendedTransactionAsync {
            val player = Player.find { Players.seat eq check }.first()
            nightEvent.donCheck = player.id
            nightEvent
        }.await()
    }

    override suspend fun addVote(dayEvent: DayEvent, voter: Int, target: Int): DayEvent {
        return suspendedTransactionAsync {
            dayEvent.votes.plus(DayVote.new {
                this.voter = Player.find { Players.seat eq voter }.first()
                this.day = dayEvent
                this.target = Player.find { Players.seat eq target }.first()
            })
            dayEvent
        }.await()
    }

    override suspend fun findStage(id: UUID): Event {
        return suspendedTransactionAsync {
            DayEvent.findById(id) ?: NightEvent.findById(id) ?: throw Exception("Stage not found")
        }.await()
    }

    override suspend fun createNewDayEvent(game: Game, currentDay: Int, newStage: DayStage, candidates: Set<Player>): DayEvent {
        return suspendedTransactionAsync {
            val dayEvent = DayEvent.new {
                this.game = game.id
                this.day = currentDay
                this.stage = newStage
            }
            dayEvent.candidates.plus(
                candidates.map {
                    DayCandidate.new {
                        this.day = dayEvent
                        this.player = it
                    }
                }
            )
            dayEvent
        }.await()
    }

    override fun removeCandidate(day: DayEvent, candidateSeat: Int): DayEvent {
        day.candidates.filter { it.player.seat == candidateSeat }.forEach { it.delete() }
        return day
    }

    override suspend fun save(event: Event) {
        suspendedTransactionAsync {
            when (event) {
                is DayEvent -> event.flush()
                is NightEvent -> event.flush()
                else -> {
                    throw Exception("Event not found")}
            }
        }.await()
    }
}
