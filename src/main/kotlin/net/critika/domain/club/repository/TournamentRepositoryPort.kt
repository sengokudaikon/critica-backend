package net.critika.domain.club.repository

import kotlinx.uuid.UUID
import net.critika.application.tournament.command.TournamentCommand
import net.critika.domain.club.model.Tournament
import net.critika.ports.CrudPort

interface TournamentRepositoryPort : CrudPort<TournamentCommand, Tournament> {
    suspend fun addUserToTournament(userId: UUID, tournamentId: UUID): Boolean
    suspend fun removeUserFromTournament(userId: UUID, tournamentId: UUID): Boolean
}
