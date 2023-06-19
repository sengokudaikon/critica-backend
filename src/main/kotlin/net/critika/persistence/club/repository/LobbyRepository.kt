package net.critika.persistence.club.repository

import kotlinx.uuid.UUID
import net.critika.application.lobby.command.LobbyCommand
import net.critika.domain.club.model.Club
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.Lobby
import net.critika.domain.club.model.Tournament
import net.critika.domain.club.repository.LobbyRepositoryPort
import net.critika.domain.user.model.User
import net.critika.infrastructure.exception.GameException
import net.critika.infrastructure.exception.LobbyException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class LobbyRepository : LobbyRepositoryPort {
    override suspend fun create(command: LobbyCommand): Lobby {
        command as LobbyCommand.Create
        return suspendedTransactionAsync {
            val lobby = Lobby.new {
                tournament = command.tournamentId?.let { Tournament.findById(it) }
                this.club = Club.findById(command.clubId) ?: throw LobbyException.NotFound("Club not found")
                this.creator = User.findById(command.creator) ?: throw LobbyException.NotFound("User not found")
                this.date = command.date.let { LocalDateTime.parse(it) }
            }
            lobby
        }.await()
    }

    override suspend fun get(id: UUID): Lobby {
        return suspendedTransactionAsync {
            val lobby = Lobby.findById(id) ?: throw LobbyException.NotFound("Lobby not found")

            lobby
        }.await()
    }

    override suspend fun list(): List<Lobby> {
        return suspendedTransactionAsync { Lobby.all().toList() }.await()
    }

    override suspend fun delete(id: UUID) {
        return suspendedTransactionAsync {
            val lobby = Lobby.findById(id) ?: throw LobbyException.NotFound("Lobby not found")
            lobby.delete()
        }.await()
    }

    override suspend fun getByGameId(gameId: UUID): Lobby {
        return suspendedTransactionAsync {
            val game = Game.findById(gameId) ?: throw GameException.NotFound("Game not found")
            val lobby = game.lobby

            lobby
        }.await()
    }

    override suspend fun update(command: LobbyCommand): Lobby {
        command as LobbyCommand.Update
        return suspendedTransactionAsync {
            val lobby = Lobby.findById(command.id) ?: throw LobbyException.NotFound("Lobby not found")
            lobby.date = LocalDateTime.parse(command.date)
            lobby.flush()
            lobby
        }.await()
    }
}
