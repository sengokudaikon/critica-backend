package net.critika.application.user.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val playerName: String,
    val email: String,
)
