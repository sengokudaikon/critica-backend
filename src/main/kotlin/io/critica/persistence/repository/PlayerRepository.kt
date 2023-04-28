package io.critica.persistence.repository

import io.critica.domain.Game
import io.critica.domain.Lobby
import io.critica.domain.Player
import io.critica.domain.User
import io.critica.persistence.db.Players
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class PlayerRepository {
    suspend fun getPlayerByUserIdAndGameId(userId: UUID, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.userId eq userId) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByPlayerIdAndGameId(playerId: UUID, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.id eq playerId) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByNameAndGameId(playerName: String, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.name eq playerName) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByPartialNameAndGameId(playerName: String, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.name like "%$playerName%") }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByName(playerName: String): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.name eq playerName }.firstOrNull()
        }.await()
    }

    suspend fun createTemporaryPlayer(playerName: String, lobbyId: UUID, gameId: UUID?): Player {
        return suspendedTransactionAsync {
            Player.new {
                this.name = playerName
                this.lobby = Lobby[lobbyId]
                this.game = gameId?.let { Game[it] }
            }
        }.await()
    }

    suspend fun create(user: User): Player {
        return suspendedTransactionAsync {
            Player.new {
                this.user = user
            }
        }.await()
    }

    suspend fun get(playerId: UUID): Player {
        return suspendedTransactionAsync { Player[playerId] }.await()
    }

    suspend fun getPlayersInGame(gameId: UUID): Flow<Player> {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId }.asFlow()
        }.await()
    }
}
