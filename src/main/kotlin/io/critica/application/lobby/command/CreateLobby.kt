package io.critica.application.lobby.command

import kotlinx.serialization.Serializable

@Serializable
data class CreateLobby(
    val date: String,
    val name: String
)
