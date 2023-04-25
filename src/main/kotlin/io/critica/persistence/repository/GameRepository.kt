package io.critica.persistence.repository

import io.critica.application.game.command.CreateGame
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.PlayerRole
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class GameRepository {
    suspend fun create(request: CreateGame): Game {
        val game = suspendedTransactionAsync {
            Game.new {
                date = request.date.toString()
                status = request.status
                winner = null
            }
        }

        return game.await()
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
                this.winner = winner?.toTeam()
            }
        }.await()
    }
}
