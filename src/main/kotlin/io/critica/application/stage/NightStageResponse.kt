package io.critica.application.stage

import io.critica.application.player.response.PlayerResponse

data class NightStageResponse(
    val dayNumber: Int,
    val mafiaShot: PlayerResponse?,
    val detectiveCheck: PlayerResponse?,
    val donCheck: PlayerResponse?,
) : StageResponse("night")
