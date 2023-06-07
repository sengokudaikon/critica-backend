package net.critika.ports.club

import net.critika.application.user.response.RatingResponse

interface RatingPort {
    suspend fun getRatingInRange(from: String, to: String): List<RatingResponse>
    suspend fun getRatingForMonth(month: Int): List<RatingResponse>
    suspend fun getRatingForWeek(week: Int): List<RatingResponse>
    suspend fun getRatingForDay(day: Int): List<RatingResponse>
    suspend fun getRatingForYear(year: Int): List<RatingResponse>
    suspend fun getRatingForSeason(): List<RatingResponse>
}