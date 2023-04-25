package io.critica.application.user.request

data class RefreshToken(
    val id: String,
    val refreshToken: String
)
