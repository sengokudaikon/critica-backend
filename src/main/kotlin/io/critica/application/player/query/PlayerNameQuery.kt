package io.critica.application.player.query

import io.critica.infrastructure.validation.ValidPlayerName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerNameQuery(
    @field:ValidPlayerName
    val name: String
)
