package io.critica.persistence.repository

import io.critica.application.lobby.request.CreateLobby
import io.critica.domain.Game
import io.critica.domain.Lobby
import io.critica.persistence.exception.GameException
import io.critica.persistence.exception.LobbyException
//import io.critica.domain.User
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class LobbyRepository {
    suspend fun create(request: CreateLobby): Lobby {
        return suspendedTransactionAsync {
            Lobby.new {
                this.date = request.date
//                this.creator = User.findById(request.creator)!!
                this.name = request.name
            }
        }.await()
    }

    suspend fun get(id: UUID): Lobby {
        return suspendedTransactionAsync {
            val lobby = Lobby.findById(id) ?: throw LobbyException.NotFound("Lobby not found")

            lobby
        }.await()
    }

    suspend fun list(): List<Lobby> {
        return suspendedTransactionAsync { Lobby.all().toList() }.await()
    }

    suspend fun delete(id: UUID) {
        return suspendedTransactionAsync {
            val lobby = Lobby.findById(id)?: throw LobbyException.NotFound("Lobby not found")
            lobby.delete()
        }.await()
    }

    suspend fun getByGameId(gameId: UUID): Lobby {
        return suspendedTransactionAsync {
            val game = Game.findById(gameId)?: throw GameException.NotFound("Game not found")
            val lobby = game.lobby

            lobby
        }.await()
    }
}
