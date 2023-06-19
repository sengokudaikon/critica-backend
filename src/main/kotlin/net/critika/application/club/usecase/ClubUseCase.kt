package net.critika.application.club.usecase

import kotlinx.uuid.UUID
import net.critika.application.club.command.ClubCommand
import net.critika.application.club.response.ClubResponse
import net.critika.application.game.response.GameResponse
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.user.response.RatingResponse
import net.critika.application.user.response.UserResponse
import net.critika.domain.club.repository.ClubRepositoryPort
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.club.ClubCrudPort
import net.critika.ports.club.ClubLobbyPort
import net.critika.ports.club.ClubMemberPort
import org.koin.core.annotation.Single

@Single
class ClubUseCase(
    private val clubRepository: ClubRepositoryPort,
    private val userRatingRepository: UserRatingRepositoryPort,
) : ClubCrudPort, ClubLobbyPort, ClubMemberPort {
    override suspend fun list(): List<ClubResponse> {
        return clubRepository.list().map { it.toResponse() }
    }
    override suspend fun getClubRating(clubId: UUID): List<RatingResponse> {
        val club = clubRepository.get(clubId)

        return userRatingRepository.findByClubId(club.id.value).map { it.toResponse() }
    }

    override suspend fun get(id: UUID): ClubResponse {
        val club = clubRepository.get(id)

        return club.toResponse()
    }

    override suspend fun create(command: ClubCommand): ClubResponse {
        val club = clubRepository.create(command)

        return club.toResponse()
    }

    override suspend fun enterClub(userId: UUID, clubId: UUID): ClubResponse {
        clubRepository.addUserToClub(userId, clubId)

        return get(clubId)
    }

    override suspend fun leaveClub(userId: UUID, clubId: UUID): ClubResponse {
        clubRepository.removeUserFromClub(userId, clubId)

        return get(clubId)
    }

    override suspend fun delete(id: UUID) {
        clubRepository.delete(id)
    }

    override suspend fun update(command: ClubCommand): ClubResponse {
        val club = clubRepository.update(command)

        return club.toResponse()
    }

    override suspend fun listLobbies(clubId: UUID): List<LobbyResponse> {
        val club = clubRepository.get(clubId)

        return club.lobbies.map { it.toResponse() }
    }

    override suspend fun listGames(clubId: UUID): List<GameResponse> {
        val club = clubRepository.get(clubId)

        return club.games.map { it.toResponse() }
    }

    override suspend fun listMembers(clubId: UUID): List<UserResponse> {
        val club = clubRepository.get(clubId)

        return club.members.map { it.toResponse() }
    }
}
