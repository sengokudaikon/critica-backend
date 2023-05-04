package net.critika.application.game.command

import net.critika.domain.GameStatus
import java.time.LocalDateTime

data class CreateGame(
    val date: LocalDateTime,
    val host: String?,
    val status: GameStatus = GameStatus.CREATED,
)
