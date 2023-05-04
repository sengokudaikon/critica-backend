package net.critika.application.player.query

import net.critika.infrastructure.validation.constraints.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class PlayerQuery(
    @ValidUUID val playerId: String
)
