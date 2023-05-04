package net.critika.usecase.admin

import net.critika.application.user.response.UserResponse
import net.critika.persistence.exception.UserException
import net.critika.persistence.repository.UserRepositoryImpl
import net.critika.persistence.repository.UserSettingsRepositoryImpl
import org.koin.core.annotation.Single
import java.util.*

@Single
class AdminUseCase(
    private val userSettingsRepository: UserSettingsRepositoryImpl,
    private val userRepository: UserRepositoryImpl,
) {
    suspend fun findUsersRequestingPromotion(): List<UserResponse> {
        return userSettingsRepository.findUsersRequestingPromotion().map { it.toResponse() }
    }

    suspend fun promoteUserToAdmin(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw UserException.NotFound("User not found")
        userRepository.promoteToAdmin(user)
        userSettingsRepository.updatePromotion(userId, null)
    }

    suspend fun rejectPromotion(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw UserException.NotFound("User not found")
        userSettingsRepository.updatePromotion(userId, false)
    }
}
