package io.critica.application.player.query

import io.critica.infrastructure.validation.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class PlayerQuery(
    @field:ValidUUID
    val id: String
)
