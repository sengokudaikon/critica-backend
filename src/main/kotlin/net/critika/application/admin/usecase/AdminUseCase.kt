package net.critika.application.admin.usecase

import kotlinx.uuid.UUID
import net.critika.application.user.response.UserResponse
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.user.repository.UserRepository
import net.critika.persistence.user.repository.UserSettingsRepository
import net.critika.ports.user.AdminPort
import org.koin.core.annotation.Single

@Single
class AdminUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val userRepository: UserRepository,
) : AdminPort {
    override suspend fun findUsersRequestingPromotion(): List<UserResponse> {
        return userSettingsRepository.findUsersRequestingPromotion().map { it.toResponse() }
    }

    override suspend fun promoteUserToHost(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw UserException.NotFound("User not found")
        userRepository.promoteToHost(user)
        userSettingsRepository.updatePromotion(userId, null)
    }

    override suspend fun rejectPromotion(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw UserException.NotFound("User not found")
        userSettingsRepository.updatePromotion(userId, false)
    }
}
