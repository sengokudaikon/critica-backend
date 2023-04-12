package io.critica.application.lobby

data class CreateLobby(
    val date: String,
    val creator: Int,
    val name: String
)