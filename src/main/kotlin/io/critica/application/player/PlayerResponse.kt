package io.critica.application.player

data class PlayerResponse(
    val id: Int,
    val name: String,
    val alive: Boolean,
    val inGame: Boolean,
    val bonusPoints: Int,
)
