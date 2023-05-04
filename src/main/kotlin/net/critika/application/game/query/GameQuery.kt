package net.critika.application.game.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidUUID

@Serializable
data class GameQuery(
    @ValidUUID val gameId: String,
)
