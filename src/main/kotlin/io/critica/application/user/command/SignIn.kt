package io.critica.application.user.command

import io.critica.infrastructure.validation.ValidPlayerName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable
import javax.annotation.Nullable

@Serializable
data class SignIn(
    @field:Nullable @field:Email
    val email: String?,
    @field:Nullable @field:ValidPlayerName
    val username: String?,
    @field:NotBlank
    val password: String,
)
