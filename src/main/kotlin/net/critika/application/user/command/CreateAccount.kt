package net.critika.application.user.command

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPassword
import net.critika.infrastructure.validation.constraints.ValidUsername

@Serializable
data class CreateAccount(
    @ValidEmail val email: String,
    @ValidPassword val password: String,
    @ValidUsername val username: String,
)
