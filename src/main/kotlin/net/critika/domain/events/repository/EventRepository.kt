package net.critika.domain.events.repository

import net.critika.application.game.response.GameResponse
import net.critika.domain.Game
import net.critika.domain.Player
import net.critika.domain.events.Event
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.model.DayStage
import net.critika.domain.events.model.NightEvent
import java.util.*

@Suppress("TooManyFunctions")
interface EventRepository {
    suspend fun startDay(game: Game, day: Int): GameResponse
    suspend fun startNight(game: Game, night: Int): GameResponse
    suspend fun addCandidate(dayEvent: DayEvent, candidate: Int): DayEvent
    suspend fun addShot(nightEvent: NightEvent, shot: Int): NightEvent

    suspend fun addVote(dayEvent: DayEvent, voter: Int, target: Int): DayEvent
    suspend fun addCheck(nightEvent: NightEvent, check: Int): NightEvent
    suspend fun addDonCheck(nightEvent: NightEvent, check: Int): NightEvent

    suspend fun findStage(id: UUID): Event

    suspend fun createNewDayEvent(game: Game, currentDay: Int, newStage: DayStage, candidates: Set<Player>): DayEvent
    fun removeCandidate(day: DayEvent, candidateSeat: Int): DayEvent

    suspend fun save(event: Event)
}
