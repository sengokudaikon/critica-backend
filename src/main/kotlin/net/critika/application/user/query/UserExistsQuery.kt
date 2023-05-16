package net.critika.application.user.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidEmail

@Serializable
data class UserExistsQuery(
    @ValidEmail val email: String?,
)
