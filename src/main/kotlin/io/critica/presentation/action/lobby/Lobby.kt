package io.critica.presentation.action.lobby

import io.critica.application.lobby.CreateLobby
import io.critica.application.lobby.DeleteLobby
import io.critica.application.lobby.GetLobby
import io.critica.domain.Lobby
import io.critica.persistence.repository.LobbyRepository

class Lobby(
    private val repository: LobbyRepository,
) {
    suspend fun create(request: CreateLobby): Lobby {
        return repository.create(request)
    }

    suspend fun get(request: GetLobby): Lobby {
        return repository.get(request)
    }

    suspend fun list(): List<Lobby> {
        return repository.list()
    }

    suspend fun delete(request: DeleteLobby) {
        return repository.delete(request)
    }
}