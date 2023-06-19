package net.critika.application.user.response

import kotlinx.serialization.Serializable
import net.critika.application.Response

@Serializable
data class UserResponse(
    val id: String,
    val playerName: String,
    val email: String,
) : Response
