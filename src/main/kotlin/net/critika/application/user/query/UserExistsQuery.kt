package net.critika.application.user.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail
import net.critika.infrastructure.validation.constraints.ValidUsername

@Serializable
data class UserExistsQuery(
    @ValidEmail val email: String?,
    @ValidUsername val username: String?,
)
