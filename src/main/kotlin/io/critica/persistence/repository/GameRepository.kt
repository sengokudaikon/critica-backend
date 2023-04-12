package io.critica.persistence.repository

import io.critica.application.game.CreateGameRequest
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Role
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class GameRepository {
    suspend fun create(request: CreateGameRequest): Game {
        val game = suspendedTransactionAsync {
            Game.new {
                date = request.date.toString()
                status = request.status
                winner = null
            }
        }

        return game.await()
    }

    suspend fun get(id: Int): Game {
        return suspendedTransactionAsync {
            Game.findById(id) ?: throw NotFoundException()
        }.await()
    }
    suspend fun getGames(): List<Game> {
        return suspendedTransactionAsync {
            Game.all().toList()
        }.await()
    }

    suspend fun update(game: Game, status: GameStatus, winner: Role? = null): Game {
        return suspendedTransactionAsync {
            game.apply {
                this.status = status
                this.winner = winner?.toTeam()
            }
        }.await()
    }
}