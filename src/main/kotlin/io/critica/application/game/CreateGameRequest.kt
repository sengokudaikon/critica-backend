package io.critica.application.game

import io.critica.domain.GameStatus
import org.joda.time.DateTime

data class CreateGameRequest(
    val date: DateTime,
    val status: GameStatus = GameStatus.WAITING
)
