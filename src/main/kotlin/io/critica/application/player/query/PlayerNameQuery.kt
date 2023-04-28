package io.critica.application.player.query

import kotlinx.serialization.Serializable

@Serializable
data class PlayerNameQuery(
    val name: String
)
