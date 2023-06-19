package net.critika.application.user.response

import kotlinx.serialization.Serializable
import net.critika.application.Response

@Serializable
data class RoleResponse(
    val role: String,
    val gamesWon: Int,
    val gamesTotal: Int,
    val bonusPoints: Int,
) : Response
