package net.critika.persistence.club.repository

import io.ktor.server.plugins.*
import kotlinx.uuid.UUID
import net.critika.application.game.command.GameCommand
import net.critika.domain.club.model.Game
import net.critika.domain.club.repository.GameRepositoryPort
import net.critika.domain.user.model.User
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single

@Single
class GameRepository : GameRepositoryPort {
    override suspend fun create(command: GameCommand): Game {
        command as GameCommand.Create
        val game = suspendedTransactionAsync {
            Game.new {
                if (command.host != null) {
                    host = User.findById(command.host) ?: throw NotFoundException()
                }
                date = command.date
                status = command.status
                winner = null
            }
        }

        return game.await()
    }

    override suspend fun update(command: GameCommand): Game {
        command as GameCommand.Update
        return suspendedTransactionAsync {
            command.game.apply {
                this.host = command.host
                this.status = command.status ?: this.status
                this.winner = command.winner?.toTeam().toString()
            }
            command.game.flush()
            command.game
        }.await()
    }

    override suspend fun delete(id: UUID) {
        suspendedTransactionAsync {
            Game.findById(id)?.delete()
        }.await()
    }

    override suspend fun get(id: UUID): Game {
        return suspendedTransactionAsync {
            Game.findById(id) ?: throw NotFoundException()
        }.await()
    }

    override suspend fun list(): List<Game> {
        return suspendedTransactionAsync {
            Game.all().toList()
        }.await()
    }
}
