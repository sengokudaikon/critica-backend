package net.critika.application.user.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidPlayerName

interface UserQuery {
    @Serializable
    data class Email(
        @ValidEmail val email: String,
    ) : UserQuery

    @Serializable
    data class Exists(
        @ValidEmail val email: String?,
    )

    @Serializable
    data class Name(
        @ValidPlayerName val playerName: String,
    )
}
