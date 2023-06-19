package net.critika.application.lobby.command

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.domain.club.model.Lobby
import net.critika.infrastructure.validation.constraints.ValidUUID

interface LobbyCommand {
    @Serializable
    data class Update(
        @ValidUUID val id: UUID,
        @ValidUUID val creator: UUID,
        val date: String,
    ) : LobbyCommand

    @Serializable
    data class Create(
        val creator: UUID,
        val date: String,
        val clubId: UUID,
        val tournamentId: UUID? = null,
    ) : LobbyCommand

    data class Save(
        val lobby: Lobby,
    ) : LobbyCommand
}
