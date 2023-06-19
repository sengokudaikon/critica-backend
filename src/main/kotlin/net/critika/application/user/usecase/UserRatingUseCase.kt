package net.critika.application.user.usecase

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRatingCommand
import net.critika.application.user.response.RatingResponse
import net.critika.domain.user.model.toTotal
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.ports.user.UserRatingPort
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class UserRatingUseCase(
    private val userRatingRepository: UserRatingRepositoryPort,
) : UserRatingPort {
    override suspend fun getRatingInRange(uuid: UUID, from: String, to: String): RatingResponse {
        return userRatingRepository.findByUser(uuid).filter { it.createdAt.toString() in from..to }.toTotal(uuid)
    }

    override suspend fun getRatingForMonth(uuid: UUID, month: Int): RatingResponse {
        return userRatingRepository.findByUser(uuid).filter { it.createdAt.monthValue == month }.toTotal(uuid)
    }

    override suspend fun getRatingForDay(uuid: UUID, day: Int): RatingResponse {
        return userRatingRepository.findByUser(uuid).filter { it.createdAt.dayOfMonth == day }.toTotal(uuid)
    }

    override suspend fun getRatingForYear(uuid: UUID, year: Int): RatingResponse {
        return userRatingRepository.findByUser(uuid).filter { it.createdAt.year == year }.toTotal(uuid)
    }

    override suspend fun getRatingForSeason(uuid: UUID): RatingResponse {
        return userRatingRepository.findByUser(uuid).filter {
            val currentDate = LocalDateTime.now()
            when (currentDate.monthValue) {
                in 10..12 -> it.createdAt.monthValue in 10..12
                in 1..3 -> it.createdAt.monthValue in 1..3
                in 4..6 -> it.createdAt.monthValue in 4..6
                in 7..9 -> it.createdAt.monthValue in 7..9
                else -> false
            }
        }.toTotal(uuid)
    }
    override suspend fun create(command: UserRatingCommand): RatingResponse {
        return userRatingRepository.create(command).toResponse()
    }

    override suspend fun get(id: UUID): RatingResponse {
        return userRatingRepository.findByUser(id).toTotal(id)
    }

    override suspend fun getUserRatingsByPlayerId(playerId: UUID): RatingResponse {
        return userRatingRepository.findUserRatingsByPlayerId(playerId).toResponse()
    }

    override suspend fun update(command: UserRatingCommand): RatingResponse {
        return userRatingRepository.update(command).toResponse()
    }

    override suspend fun delete(id: UUID) {
        userRatingRepository.delete(id)
    }

    override suspend fun list(): List<RatingResponse> {
        return userRatingRepository.list().map { it.toResponse() }
    }
}
