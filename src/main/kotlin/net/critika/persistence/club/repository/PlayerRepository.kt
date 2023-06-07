package net.critika.persistence.club.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.uuid.UUID
import net.critika.application.player.command.PlayerCommand
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.Lobby
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.gameprocess.repository.PlayerRepositoryPort
import net.critika.persistence.gameprocess.entity.Players
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single

@Single
class PlayerRepository : PlayerRepositoryPort {
    override suspend fun getPlayerByUserIdAndGameId(userId: UUID, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.userId eq userId) }.firstOrNull()
        }.await()
    }

    override suspend fun getPlayerByPlayerIdAndGameId(playerId: UUID, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.id eq playerId) }.firstOrNull()
        }.await()
    }

    override suspend fun getPlayerByNameAndGameId(playerName: String, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.name eq playerName) }.firstOrNull()
        }.await()
    }

    override suspend fun getPlayerByPartialNameAndGameId(playerName: String, gameId: UUID): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.name like "%$playerName%") }.firstOrNull()
        }.await()
    }

    override suspend fun getPlayerByName(playerName: String): Player? {
        return suspendedTransactionAsync {
            Player.find { Players.name eq playerName }.firstOrNull()
        }.await()
    }

    override suspend fun createTemporaryPlayer(playerName: String, lobbyId: UUID, gameId: UUID?): Player {
        return suspendedTransactionAsync {
            Player.new {
                this.name = playerName
                this.lobby = Lobby[lobbyId]
                this.game = gameId?.let { Game[it] }
            }
        }.await()
    }

    override suspend fun create(command: PlayerCommand): Player {
        command as PlayerCommand.Create
        return suspendedTransactionAsync {
            Player.new {
                this.user = command.user
                this.name = command.playerName
                this.lobby = Lobby[command.lobbyId]
            }
        }.await()
    }

    override suspend fun get(id: UUID): Player {
        return suspendedTransactionAsync { Player[id] }.await()
    }

    override suspend fun getPlayersInGame(gameId: UUID): Flow<Player> {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId }.asFlow()
        }.await()
    }

    override suspend fun getPlayerByUserId(userId: UUID): Player? {
        return suspendedTransactionAsync {
            val player = Player.find { Players.userId eq userId }.firstOrNull()

            player
        }.await()
    }

    override suspend fun getPlayerInGameBySeat(gameId: UUID, seat: Int): Player {
        return suspendedTransactionAsync {
            Player.find { Players.gameId eq gameId and (Players.seat eq seat) }.first()
        }.await()
    }

    override suspend fun list(): List<Player> {
        return suspendedTransactionAsync { Player.all().toList() }.await()
    }

    override suspend fun update(command: PlayerCommand): Player {
        command as PlayerCommand.Update
        return suspendedTransactionAsync {
            val player = Player[command.id]
            player.name = command.playerName
            player.flush()
            player
        }.await()
    }

    override suspend fun delete(id: UUID) {
        suspendedTransactionAsync {
            Player[id].delete()
        }.await()
    }
}
