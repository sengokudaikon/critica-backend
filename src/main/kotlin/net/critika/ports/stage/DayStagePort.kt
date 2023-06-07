package net.critika.ports.stage

import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.domain.gameprocess.model.DayEvent

interface DayStagePort {
    suspend fun startDay(gameId: UUID, day: Int): GameResponse
    suspend fun finishDay(day: DayEvent): GameResponse
    suspend fun addCandidate(dayId: UUID, candidateSeat: Int): GameResponse

    suspend fun removeCandidate(dayId: UUID, candidateSeat: Int): GameResponse

    suspend fun voteOnCandidate(dayId: UUID, candidateId: Int, voterId: Int): GameResponse
}