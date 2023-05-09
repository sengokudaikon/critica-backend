package net.critika.domain.user.repository

import net.critika.domain.user.model.User
import java.util.*

interface UserRepository {
    suspend fun findByUsername(playerName: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: UUID): User?
    suspend fun create(uid: String, userName: String, email: String, playerName: String, password: String): User
    suspend fun promoteToOwner(user: User): User
    suspend fun promoteToHost(user: User): User
    suspend fun updateUsername(userId: UUID, newUsername: String)
    suspend fun updatePassword(userId: UUID, newPassword: String)
    suspend fun updateEmail(userId: UUID, email: String)
    suspend fun updatePlayerName(userId: UUID, playerName: String)
    suspend fun findByUid(uid: String): User?
    suspend fun addDeviceToken(userId: UUID, deviceToken: String)
}
