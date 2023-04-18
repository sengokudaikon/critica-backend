package io.critica.usecase.lobby

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.application.lobby.response.LobbyResponse
import io.critica.persistence.repository.LobbyRepository

class LobbyCrud(
    private val repository: LobbyRepository
) {
    suspend fun create(request: CreateLobby): LobbyResponse {
        return repository.create(request).toResponse()
    }

    suspend fun get(request: GetLobby): LobbyResponse {
        return repository.get(request).toResponse()
    }

    suspend fun list(): List<LobbyResponse> {
        return repository.list().map { it.toResponse() }
    }

    suspend fun delete(request: DeleteLobby) {
        return repository.delete(request)
    }
}
