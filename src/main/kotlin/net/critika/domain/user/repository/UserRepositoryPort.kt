package net.critika.domain.user.repository

import kotlinx.uuid.UUID
import net.critika.domain.user.model.User

interface UserRepositoryPort {
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: UUID): User?
    suspend fun create(uid: String, email: String, playerName: String): User
    suspend fun promoteToOwner(user: User): User
    suspend fun promoteToHost(user: User): User
    suspend fun updatePlayerName(userId: UUID, playerName: String): User
    suspend fun findByUid(uid: String): User?
    suspend fun findByPlayerName(playerName: String): User
}
