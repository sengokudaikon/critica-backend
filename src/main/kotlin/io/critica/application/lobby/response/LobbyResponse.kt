package io.critica.application.lobby.response

import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(
    val id: String,
    val date: String,
    val name: String,
    val creator: String,
)
