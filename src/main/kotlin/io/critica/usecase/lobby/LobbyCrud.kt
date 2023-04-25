package io.critica.usecase.lobby

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.response.LobbyResponse
import io.critica.persistence.repository.LobbyRepository
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class LobbyCrud(
    private val repository: LobbyRepository
) {
    suspend fun create(request: CreateLobby): LobbyResponse {
        return repository.create(request).toResponse()
    }

    suspend fun get(id: UUID): LobbyResponse {
        return repository.get(id).toResponse()
    }

    suspend fun list(): List<LobbyResponse> {
        return repository.list().map { it.toResponse() }
    }

    suspend fun delete(id: UUID) {
        return repository.delete(id)
    }
}
