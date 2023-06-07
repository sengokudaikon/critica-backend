package net.critika.ports.stage

import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.domain.club.model.Game
import net.critika.domain.gameprocess.model.DayEvent
import net.critika.domain.gameprocess.model.Player

interface StagePort: DayStagePort, NightStagePort {
    suspend fun firstShot(stageId: UUID, shot: Int, bestMove: List<Int>): GameResponse
    suspend fun finishStage(stageId: UUID): GameResponse
    suspend fun nextStage(stageId: UUID): GameResponse

    suspend fun selectDayStage(day: DayEvent): GameResponse

    suspend fun previousStage(stageId: UUID): GameResponse

    suspend fun addFoul(gameId: UUID, seat: Int): Player

    suspend fun addBonus(gameId: UUID, seat: Int): Player

    suspend fun opw(gameId: UUID, seat: Int): Game
}