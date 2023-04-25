package io.critica.application.game.command

import io.critica.domain.GameStatus
import org.joda.time.DateTime

data class CreateGame(
    val date: DateTime,
    val status: GameStatus = GameStatus.WAITING
)
