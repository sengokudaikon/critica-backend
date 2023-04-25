package io.critica.application.user.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccount(
    val email: String,
    val password: String,
    val username: String,
)
