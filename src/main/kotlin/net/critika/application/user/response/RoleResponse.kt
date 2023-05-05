package net.critika.application.user.response

import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    val id: String,
    val role: String,
    val gamesWon: Int,
    val gamesTotal: Int,
    val bonusPoints: Int,
)
