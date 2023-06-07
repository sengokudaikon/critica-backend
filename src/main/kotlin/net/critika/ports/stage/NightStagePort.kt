package net.critika.ports.stage

import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.domain.gameprocess.model.NightEvent

interface NightStagePort {
    suspend fun startNight(gameId: UUID, night: Int): GameResponse
    suspend fun finishNight(night: NightEvent): GameResponse

    suspend fun setShot(nightId: UUID, shotId: Int): GameResponse

    suspend fun setCheck(nightId: UUID, checkedId: Int): GameResponse
    suspend fun setDonCheck(nightId: UUID, donCheckId: Int): GameResponse
}