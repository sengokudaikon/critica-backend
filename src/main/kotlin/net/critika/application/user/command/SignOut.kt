package net.critika.application.user.command

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidUUID

@Serializable
data class SignOut(
    @ValidUUID val id: String,
    val refreshToken: String,
)
