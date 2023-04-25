package io.critica.application.user.request

import kotlinx.serialization.Serializable

@Serializable
data class SignIn(
    val email: String?,
    val username: String?,
    val password: String,
)
