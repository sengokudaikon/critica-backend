package net.critika.ports.club

import net.critika.application.user.response.RatingResponse

interface RatingPort<U> {
    suspend fun getRatingInRange(uuid: U, from: String, to: String): RatingResponse
    suspend fun getRatingForMonth(uuid: U, month: Int): RatingResponse
    suspend fun getRatingForDay(uuid: U, day: Int): RatingResponse
    suspend fun getRatingForYear(uuid: U, year: Int): RatingResponse
    suspend fun getRatingForSeason(uuid: U): RatingResponse
}
