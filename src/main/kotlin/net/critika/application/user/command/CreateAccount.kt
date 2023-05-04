package net.critika.application.user.command

import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPassword
import net.critika.infrastructure.validation.constraints.ValidPlayerName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccount(
    @ValidEmail val email: String,
    @ValidPassword val password: String,
    @ValidPlayerName val username: String,
)