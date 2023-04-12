package io.critica.application.lobby.response

import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class LobbyResponse(
    val id: Int,
    val date: String,
    val name: String,
)
