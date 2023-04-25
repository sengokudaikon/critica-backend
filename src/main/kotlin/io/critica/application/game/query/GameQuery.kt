package io.critica.application.game.query

import io.critica.infrastructure.validation.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class GameQuery(
    @field:ValidUUID
    val id: String
)