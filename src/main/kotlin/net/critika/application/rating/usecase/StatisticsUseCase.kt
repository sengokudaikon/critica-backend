package net.critika.application.rating.usecase

import net.critika.application.user.response.RatingResponse
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.club.RatingPort

class StatisticsUseCase(
    private val userRatingRepository: UserRatingRepositoryPort,
): RatingPort {

    override suspend fun getRatingInRange(from: String, to: String): List<RatingResponse> {
        return userRatingRepository.findInRange(from, to).map { it.toResponse() }
    }

    override suspend fun getRatingForMonth(month: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByMonth(month).map { it.toResponse() }
    }

    override suspend fun getRatingForWeek(week: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByWeek(week).map { it.toResponse() }
    }

    override suspend fun getRatingForDay(day: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByDay(day).map { it.toResponse() }
    }

    override suspend fun getRatingForYear(year: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByYear(year).map { it.toResponse() }
    }

    override suspend fun getRatingForSeason(): List<RatingResponse> {
        return userRatingRepository.findCurrentSeasonUserRatings().map { it.toResponse() }
    }
}
