package net.critika.application.user.command

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPassword
import net.critika.infrastructure.validation.constraints.ValidPlayerName
import net.critika.infrastructure.validation.constraints.ValidUUID
import net.critika.infrastructure.validation.constraints.ValidUsername

interface UserCommand {
    @Serializable
    data class Create(
        @ValidEmail val email: String,
        @ValidPassword val password: String,
        @ValidUsername val username: String,
        @ValidPlayerName val playerName: String,
    )

    @Serializable
    data class SignIn(
        @ValidEmail val email: String?,
        @ValidUsername val username: String?,
        @ValidPassword val password: String,
    )

    @Serializable
    data class RefreshToken(
        val refreshToken: String,
    )

    @Serializable
    data class SignInWithProvider(
        @ValidEmail val email: String,
        val deviceToken: String,
    )

    @Serializable
    data class SignOut(
        @ValidUUID val id: UUID,
        val refreshToken: String,
    )
}
