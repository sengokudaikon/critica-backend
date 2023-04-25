package io.critica.application.player.command

data class PlayerRequest(
    val userId: Int,
    val name: String,
    val role: String,
    val status: String
)
