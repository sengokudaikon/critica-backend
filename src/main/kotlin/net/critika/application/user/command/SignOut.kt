package net.critika.application.user.command

import net.critika.infrastructure.validation.constraints.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class SignOut(
    @ValidUUID val id: String,
    val refreshToken: String,
)
