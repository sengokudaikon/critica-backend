package io.critica.application.user.command

import io.critica.infrastructure.validation.ValidUUID
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    @field:ValidUUID
    val id: String,
    @field:NotBlank
    val refreshToken: String
)
