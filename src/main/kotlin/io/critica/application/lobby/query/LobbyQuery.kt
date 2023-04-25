package io.critica.application.lobby.query

import io.critica.infrastructure.validation.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class LobbyQuery(
    @field:ValidUUID
    val id: String
)
