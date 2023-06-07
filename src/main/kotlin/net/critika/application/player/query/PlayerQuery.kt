package net.critika.application.player.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidUUID

@Serializable
data class PlayerQuery(
    @ValidUUID val playerId: UUID,
)
