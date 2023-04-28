package io.critica.application.user.command

import kotlinx.serialization.Serializable

@Serializable
data class SignIn(
    val email: String?,
    val username: String?,
    val password: String,
)
