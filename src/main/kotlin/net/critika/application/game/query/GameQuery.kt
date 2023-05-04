package net.critika.application.game.query

import net.critika.infrastructure.validation.constraints.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class GameQuery(
    @ValidUUID val gameId: String
)