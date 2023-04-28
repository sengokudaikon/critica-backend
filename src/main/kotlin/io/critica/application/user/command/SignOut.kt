package io.critica.application.user.command

import kotlinx.serialization.Serializable

@Serializable
data class SignOut(
    val id: String,
    val refreshToken: String,
)
