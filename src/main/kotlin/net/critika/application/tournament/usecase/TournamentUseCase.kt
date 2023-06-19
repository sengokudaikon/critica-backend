package net.critika.application.tournament.usecase

import kotlinx.uuid.UUID
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.player.response.PlayerResponse
import net.critika.application.tournament.command.TournamentCommand
import net.critika.application.tournament.response.TournamentResponse
import net.critika.application.user.response.RatingResponse
import net.critika.domain.club.repository.TournamentRepositoryPort
import net.critika.domain.user.model.toTotal
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.tournament.TournamentCrudPort
import net.critika.ports.tournament.TournamentLobbyPort
import net.critika.ports.tournament.TournamentPlayerPort
import org.koin.core.annotation.Single

@Single
class TournamentUseCase(
    private val tournamentRepository: TournamentRepositoryPort,
    private val userRatingRepository: UserRatingRepositoryPort,
) : TournamentCrudPort, TournamentPlayerPort, TournamentLobbyPort {
    override suspend fun create(command: TournamentCommand): TournamentResponse {
        return tournamentRepository.create(command).toResponse()
    }

    override suspend fun update(command: TournamentCommand): TournamentResponse {
        return tournamentRepository.update(command).toResponse()
    }

    override suspend fun delete(id: UUID) {
        return tournamentRepository.delete(id)
    }

    override suspend fun get(id: UUID): TournamentResponse {
        return tournamentRepository.get(id).toResponse()
    }

    override suspend fun list(): List<TournamentResponse> {
        return tournamentRepository.list().map { it.toResponse() }
    }

    override suspend fun listLobbies(clubId: UUID): List<LobbyResponse> {
        return tournamentRepository.get(clubId).lobbies.map { it.toResponse() }
    }

    override suspend fun listGames(clubId: UUID): List<GameResponse> {
        return tournamentRepository.get(clubId).games.map { it.toResponse() }
    }

    override suspend fun getPlayers(tournamentId: UUID): List<PlayerResponse> {
        return tournamentRepository.get(tournamentId).players.map { it.toResponse() }
    }

    override suspend fun join(tournamentId: UUID, userId: UUID): TournamentResponse {
        tournamentRepository.addUserToTournament(tournamentId, userId)
        return tournamentRepository.get(tournamentId).toResponse()
    }

    override suspend fun leave(tournamentId: UUID, userId: UUID): TournamentResponse {
        tournamentRepository.removeUserFromTournament(tournamentId, userId)
        return tournamentRepository.get(tournamentId).toResponse()
    }

    override suspend fun getRating(id: UUID): RatingResponse {
        return userRatingRepository.findByTournament(id).toTotal(id)
    }
}
