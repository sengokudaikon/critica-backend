package net.critika.usecase.lobby

import net.critika.application.lobby.response.LobbyResponse
import net.critika.persistence.repository.LobbyRepository
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.joda.time.DateTime
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.*

@Single
class LobbyCrudUseCase(
    private val repository: LobbyRepository
) {
    suspend fun create(creator: UUID, date: LocalDateTime): LobbyResponse {
        return suspendedTransactionAsync {
            val result = repository.create(creator, date)
            result.toResponse()
        }.await()
    }

    suspend fun get(id: UUID): LobbyResponse {
        return suspendedTransactionAsync {
            repository.get(id).toResponse()
        }.await()
    }

    suspend fun list(): List<LobbyResponse> {
        return suspendedTransactionAsync {
            repository.list().map { it.toResponse() }
        }.await()
    }

    suspend fun delete(id: UUID) {
        return repository.delete(id)
    }
}
