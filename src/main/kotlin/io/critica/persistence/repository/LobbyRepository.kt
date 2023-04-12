package io.critica.persistence.repository

import io.critica.application.lobby.CreateLobby
import io.critica.application.lobby.DeleteLobby
import io.critica.application.lobby.GetLobby
import io.critica.domain.Lobby
//import io.critica.domain.User
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

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

    suspend fun get(request: GetLobby): Lobby {
        return suspendedTransactionAsync { Lobby.findById(request.id)!!}.await()
    }

    suspend fun list(): List<Lobby> {
        return suspendedTransactionAsync { Lobby.all().toList() }.await()
    }

    suspend fun delete(request: DeleteLobby) {
        return suspendedTransactionAsync { Lobby.findById(request.id)!!.delete() }.await()
    }
}