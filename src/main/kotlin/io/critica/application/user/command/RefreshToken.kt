package io.critica.application.user.command

import io.critica.infrastructure.validation.constraints.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    @ValidUUID val id: String,
    val refreshToken: String
)
