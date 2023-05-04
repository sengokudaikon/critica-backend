package net.critika.application.lobby.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidUUID

@Serializable
data class LobbyQuery(
    @ValidUUID val lobbyId: UUID,
)
