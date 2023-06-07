package net.critika.application.lobby.usecase

import kotlinx.uuid.UUID
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.lobby.response.LobbyResponse
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.ports.lobby.LobbyCrudPort
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single

@Single
class LobbyCrudUseCase(
    private val repository: LobbyRepository,
) : LobbyCrudPort {
    override suspend fun create(command: LobbyCommand): LobbyResponse {
        return suspendedTransactionAsync {
            command as LobbyCommand.Create
            val result = repository.create(command)
            result.toResponse()
        }.await()
    }

    override suspend fun get(id: UUID): LobbyResponse {
        return suspendedTransactionAsync {
            repository.get(id).toResponse()
        }.await()
    }

    override suspend fun list(): List<LobbyResponse> {
        return suspendedTransactionAsync {
            repository.list().map { it.toResponse() }
        }.await()
    }

    override suspend fun delete(id: UUID) {
        return repository.delete(id)
    }

    override suspend fun update(command: LobbyCommand): LobbyResponse {
        return suspendedTransactionAsync {
            command as LobbyCommand.Update
            repository.update(command)
            repository.get(command.id).toResponse()
        }.await()
    }
}
