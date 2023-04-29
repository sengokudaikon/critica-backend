package io.critica.application.user.command

import io.critica.infrastructure.validation.constraints.ValidEmail
import io.critica.infrastructure.validation.constraints.ValidPassword
import io.critica.infrastructure.validation.constraints.ValidUsername
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccount(
    @ValidEmail val email: String,
    @ValidPassword val password: String,
    @ValidUsername val username: String,
)