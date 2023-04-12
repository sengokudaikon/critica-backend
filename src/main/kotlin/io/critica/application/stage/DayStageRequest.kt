package io.critica.application.stage

import io.critica.domain.events.DayEvent

class DayStageRequest(
    val gameId: Int,
    val dayEvent: DayEvent
)
