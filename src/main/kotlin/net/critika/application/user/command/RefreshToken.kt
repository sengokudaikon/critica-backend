package net.critika.application.user.command

import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    val refreshToken: String,
)
