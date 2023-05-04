package net.critika.persistence.repository

// import net.critika.domain.user.model.User
import net.critika.domain.Game
import net.critika.domain.Lobby
import net.critika.domain.user.model.User
import net.critika.persistence.exception.GameException
import net.critika.persistence.exception.LobbyException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.*

@Single
class LobbyRepository {
    suspend fun create(creator: UUID, date: LocalDateTime): Lobby {
        return suspendedTransactionAsync {
            val lobby = Lobby.new {
                this.creator = User.findById(creator) ?: throw LobbyException.NotFound("User not found")
                this.date = date
            }
            lobby
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
            val lobby = Lobby.findById(id) ?: throw LobbyException.NotFound("Lobby not found")
            lobby.delete()
        }.await()
    }

    suspend fun getByGameId(gameId: UUID): Lobby {
        return suspendedTransactionAsync {
            val game = Game.findById(gameId) ?: throw GameException.NotFound("Game not found")
            val lobby = game.lobby

            lobby
        }.await()
    }

    suspend fun save(lobby: Lobby): Lobby {
        return suspendedTransactionAsync {
            lobby.flush()
            lobby
        }.await()
    }
}
