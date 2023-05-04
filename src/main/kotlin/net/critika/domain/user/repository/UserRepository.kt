package net.critika.domain.user.repository

import net.critika.domain.user.model.User
import java.util.*

interface UserRepository {
    suspend fun findByUsername(playerName: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: UUID): User?
    suspend fun create(userName: String, email: String, password: String): User
    suspend fun promoteToOwner(user: User): User
    suspend fun promoteToAdmin(user: User): User
    suspend fun updateUsername(userId: UUID, newUsername: String)
    suspend fun updatePassword(userId: UUID, newPassword: String)
    suspend fun updateEmail(userId: UUID, email: String)
}
