package net.critika.usecase.stats

import net.critika.application.user.response.RatingResponse
import net.critika.domain.user.repository.UserRatingRepository

class StatisticsUseCase(
    private val userRatingRepository: UserRatingRepository,
) {

    suspend fun getRatingForMonth(month: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByMonth(month).map { it.toResponse() }
    }

    suspend fun getRatingForWeek(week: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByWeek(week).map { it.toResponse() }
    }

    suspend fun getRatingForDay(day: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByDay(day).map { it.toResponse() }
    }

    suspend fun getRatingForYear(year: Int): List<RatingResponse> {
        return userRatingRepository.findUserRatingsByYear(year).map { it.toResponse() }
    }

    suspend fun getRatingForSeason(): List<RatingResponse> {
        return userRatingRepository.findCurrentSeasonUserRatings().map { it.toResponse() }
    }
}
