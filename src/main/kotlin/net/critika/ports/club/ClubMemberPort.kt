package net.critika.ports.club

import kotlinx.uuid.UUID
import net.critika.application.club.response.ClubResponse
import net.critika.application.user.response.RatingResponse
import net.critika.application.user.response.UserResponse

interface ClubMemberPort {
    suspend fun getClubRating(clubId: UUID): List<RatingResponse>
    suspend fun enterClub(userId: UUID, clubId: UUID): ClubResponse
    suspend fun leaveClub(userId: UUID, clubId: UUID): ClubResponse
    suspend fun listMembers(clubId: UUID): List<UserResponse>
}
