package io.critica.application.game

import io.critica.domain.GameStatus

data class CreateGameRequest(
    val date: String,
    val status: GameStatus = GameStatus.CREATED
)