package net.critika.application.stage.response

import net.critika.application.player.response.PlayerResponse

data class NightStageResponse(
    val dayNumber: Int,
    val mafiaShot: PlayerResponse?,
    val detectiveCheck: PlayerResponse?,
    val donCheck: PlayerResponse?,
) : StageResponse("night")
