package io.critica.application.stage

class NightStageRequest(
    val gameId: Int,
    val dayNumber: Int,
    val mafiaShot: Int?,
    val detectiveCheck: Int?,
    val donCheck: Int?,
    val playerRemoved: Int?
) {
}