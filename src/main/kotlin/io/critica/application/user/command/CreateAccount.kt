package io.critica.application.user.command

import io.critica.infrastructure.validation.SecurePassword
import io.critica.infrastructure.validation.ValidPlayerName
import jakarta.validation.constraints.Email
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccount(
    @field:Email
    val email: String,
    @field:SecurePassword
    val password: String,
    @field:ValidPlayerName
    val username: String,
)
