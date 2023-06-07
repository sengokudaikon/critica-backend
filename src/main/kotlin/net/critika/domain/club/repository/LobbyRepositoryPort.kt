package net.critika.domain.club.repository

import kotlinx.uuid.UUID
import net.critika.application.lobby.command.LobbyCommand
import net.critika.domain.club.model.Lobby
import net.critika.ports.CrudPort

interface LobbyRepositoryPort : CrudPort<LobbyCommand, Lobby> {
    suspend fun getByGameId(gameId: UUID): Lobby
}
