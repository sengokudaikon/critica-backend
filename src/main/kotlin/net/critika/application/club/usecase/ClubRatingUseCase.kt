package net.critika.application.club.usecase

import kotlinx.uuid.UUID
import net.critika.application.user.response.RatingResponse
import net.critika.domain.user.model.toTotal
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.club.ClubRatingPort
import org.koin.core.annotation.Single
import java.time.LocalDateTime.now

@Single
class ClubRatingUseCase(
    private val userRatingRepository: UserRatingRepositoryPort,
) : ClubRatingPort {
    override suspend fun getRatingInRange(uuid: UUID, from: String, to: String): RatingResponse {
        return userRatingRepository.findByClubId(uuid).filter { it.createdAt.toString() in from..to }.toTotal(uuid)
    }

    override suspend fun getRatingForMonth(uuid: UUID, month: Int): RatingResponse {
        return userRatingRepository.findByClubId(uuid).filter { it.createdAt.monthValue == month }.toTotal(uuid)
    }

    override suspend fun getRatingForDay(uuid: UUID, day: Int): RatingResponse {
        return userRatingRepository.findByClubId(uuid).filter { it.createdAt.dayOfMonth == day }.toTotal(uuid)
    }

    override suspend fun getRatingForYear(uuid: UUID, year: Int): RatingResponse {
        return userRatingRepository.findByClubId(uuid).filter { it.createdAt.year == year }.toTotal(uuid)
    }

    override suspend fun getRatingForSeason(uuid: UUID): RatingResponse {
        return userRatingRepository.findByClubId(uuid).filter {
            val currentDate = now()
            when (currentDate.monthValue) {
                in 10..12 -> it.createdAt.monthValue in 10..12
                in 1..3 -> it.createdAt.monthValue in 1..3
                in 4..6 -> it.createdAt.monthValue in 4..6
                in 7..9 -> it.createdAt.monthValue in 7..9
                else -> false
            }
        }.toTotal(uuid)
    }
}
