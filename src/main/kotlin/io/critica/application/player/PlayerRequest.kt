package io.critica.application.player

data class PlayerRequest(
    val userId: Int,
    val name: String,
    val role: String,
    val status: String
) {
}