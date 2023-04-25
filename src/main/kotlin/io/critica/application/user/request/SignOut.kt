package io.critica.application.user.request

data class SignOut(
    val id: String,
    val refreshToken: String,
)
