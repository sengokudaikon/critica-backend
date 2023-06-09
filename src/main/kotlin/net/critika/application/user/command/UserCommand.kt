package net.critika.application.user.command

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPlayerName

interface UserCommand {
    @Serializable
    data class Create(
        @ValidEmail val email: String,
        @ValidPlayerName val playerName: String,
    )

    @Serializable
    data class SignIn(
        @ValidEmail val email: String?,
    )
}
