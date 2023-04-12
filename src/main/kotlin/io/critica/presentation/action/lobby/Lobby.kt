package io.critica.presentation.action.lobby

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.application.lobby.response.LobbyResponse
import io.critica.domain.Game
import io.critica.domain.Lobby
import io.critica.domain.Player
import io.critica.persistence.GameProcessor
import io.critica.persistence.repository.LobbyRepository
import org.joda.time.LocalTime

class Lobby(
    private val repository: LobbyRepository,
    private val preparation: GameProcessor.Preparation = GameProcessor.Preparation()
) {
    suspend fun create(request: CreateLobby): LobbyResponse {
        return repository.create(request).toResponse()
    }

    suspend fun get(request: GetLobby): LobbyResponse {
        return repository.get(request).toResponse()
    }

    suspend fun addGame(id: Int, time: LocalTime): Game {
        return preparation.addGameToLobby(id, time)
    }

    suspend fun removeGame(id: Int, gameId: Int) {
        return preparation.removeGameFromLobby(id, gameId)
    }

//    suspend fun addPlayer(id: Int, playerName: String): Player {
//        return preparation.addPlayerToLobby(id, playerName)
//    }
//
//    suspend fun addPlayerById(id: Int, playerId: Int): Player {
//        return preparation.addPlayerToLobbyById(id, playerId)
//    }
//
//    suspend fun removePlayer(id: Int, playerName: String): Player {
//        return preparation.removePlayerFromLobby(id, playerName)
//    }

    suspend fun list(): List<LobbyResponse> {
        return repository.list().map { it.toResponse() }
    }

    suspend fun delete(request: DeleteLobby) {
        return repository.delete(request)
    }

    fun Lobby.toResponse(): LobbyResponse {
        return LobbyResponse(
            id = this.id.value,
            date = this.date,
            name = this.name,
        )
    }
}
