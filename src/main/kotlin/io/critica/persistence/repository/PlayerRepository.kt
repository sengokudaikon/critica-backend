package io.critica.persistence.repository

import io.critica.domain.Game
import io.critica.domain.Player
import io.critica.domain.User
import io.critica.persistence.db.Players
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class PlayerRepository {
    suspend fun getPlayerByUserIdAndGameId(userId: Int, gameId: Int): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.userId eq userId) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByPlayerIdAndGameId(playerId: Int, gameId: Int): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.id eq playerId) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByNameAndGameId(playerName: String, gameId: Int): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.name eq playerName) }.firstOrNull()
        }.await()
    }

    suspend fun getPlayerByName(playerName: String): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.name eq playerName }.firstOrNull()
        }.await()
    }

    suspend fun createTemporaryPlayer(gameId: Int, playerName: String): Player {
        return suspendedTransactionAsync {
            Player.new {
                this.game = Game[gameId]
                this.name = playerName
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

    suspend fun get(playerId: Int): Player {
        return suspendedTransactionAsync { Player[playerId] }.await()
    }
}
