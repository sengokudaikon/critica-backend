package net.critika.persistence.repository

import io.ktor.server.plugins.*
import net.critika.application.game.command.CreateGame
import net.critika.domain.Game
import net.critika.domain.GameStatus
import net.critika.domain.PlayerRole
import net.critika.domain.user.model.User
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.*

@Single
class GameRepository {
    suspend fun create(request: CreateGame): Game {
        val game = suspendedTransactionAsync {
            Game.new {
                if (request.host != null) {
                    host = User.findById(UUID.fromString(request.host)) ?: throw NotFoundException()
                }
                date = request.date
                status = request.status
                winner = null
            }
        }

        return game.await()
    }

    suspend fun assignHost(game: Game, host: User): Game {
        return suspendedTransactionAsync {
            game.apply {
                this.host = host
            }
        }.await()
    }

    suspend fun get(id: UUID): Game {
        return suspendedTransactionAsync {
            Game.findById(id) ?: throw NotFoundException()
        }.await()
    }

    suspend fun getGames(): List<Game> {
        return suspendedTransactionAsync {
            Game.all().toList()
        }.await()
    }

    suspend fun update(game: Game, status: GameStatus, winner: PlayerRole? = null): Game {
        return suspendedTransactionAsync {
            game.apply {
                this.status = status
                this.winner = winner?.toTeam().toString()
            }
        }.await()
    }

    suspend fun save(game: Game): Game {
        return suspendedTransactionAsync {
            game.flush()
            game
        }.await()
    }
}
