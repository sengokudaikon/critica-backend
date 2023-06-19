package net.critika.persistence.club.repository

import kotlinx.uuid.UUID
import net.critika.application.tournament.command.TournamentCommand
import net.critika.domain.club.model.Club
import net.critika.domain.club.model.Tournament
import net.critika.domain.club.repository.TournamentRepositoryPort
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.persistence.gameprocess.entity.Players
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class TournamentRepository : TournamentRepositoryPort {
    override suspend fun addUserToTournament(userId: UUID, tournamentId: UUID): Boolean {
        return suspendedTransactionAsync {
            val tournament = Tournament.findById(tournamentId) ?: return@suspendedTransactionAsync false
            tournament.players.plus(Player.find { Players.userId eq userId })
            true
        }.await()
    }

    override suspend fun removeUserFromTournament(userId: UUID, tournamentId: UUID): Boolean {
        return suspendedTransactionAsync {
            val tournament = Tournament.findById(tournamentId) ?: return@suspendedTransactionAsync false
            tournament.players.minus(Player.find { Players.userId eq userId })
            true
        }.await()
    }

    override suspend fun create(command: TournamentCommand): Tournament {
        return suspendedTransactionAsync {
            command as TournamentCommand.Create
            Tournament.new {
                name = command.name
                creator = User.findById(command.creatorId) ?: throw Exception("User not found")
                description = command.description
                location = command.location
                date = LocalDateTime.parse(command.startDate)
                playerLimit = command.playerLimit
                createdAt = LocalDateTime.now()
                club = command.clubId?.let { Club.findById(it) }
            }
        }.await()
    }

    override suspend fun update(command: TournamentCommand): Tournament {
        return suspendedTransactionAsync {
            command as TournamentCommand.Update
            val tournament = Tournament[command.id]
            tournament.name = command.name ?: tournament.name
            tournament.description = command.description ?: tournament.description
            tournament.location = command.location ?: tournament.location
            tournament.date = command.startDate.let { LocalDateTime.parse(it) } ?: tournament.date
            tournament.playerLimit = command.playerLimit ?: tournament.playerLimit
            tournament.updatedAt = LocalDateTime.now()
            tournament
        }.await()
    }

    override suspend fun delete(id: UUID) {
        suspendedTransactionAsync {
            Tournament.findById(id)?.delete() ?: throw Exception("Tournament not found")
        }.await()
    }

    override suspend fun get(id: UUID): Tournament {
        return suspendedTransactionAsync {
            Tournament.findById(id) ?: throw Exception("Tournament not found")
        }.await()
    }

    override suspend fun list(): List<Tournament> {
        return suspendedTransactionAsync {
            Tournament.all().toList()
        }.await()
    }
}
