package net.critika.application.game.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidUUID

@Serializable
data class GameQuery(
    @ValidUUID val hostId: UUID,
    @ValidUUID val gameId: UUID,
)
