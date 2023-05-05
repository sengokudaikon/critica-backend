package net.critika.application.user.command

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPassword
import net.critika.infrastructure.validation.constraints.ValidPlayerName
import net.critika.infrastructure.validation.constraints.ValidUsername

@Serializable
data class SignIn(
    @ValidEmail val email: String?,
    @ValidUsername val username: String?,
    @ValidPlayerName val playerName: String,
    @ValidPassword val password: String,
)
