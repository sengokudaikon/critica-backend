package net.critika.ports.user

import kotlinx.uuid.UUID
import net.critika.application.user.response.UserResponse

interface AdminPort {
    suspend fun findUsersRequestingPromotion(): List<UserResponse>
    suspend fun promoteUserToHost(userId: UUID)
    suspend fun rejectPromotion(userId: UUID)
}