package net.critika.application.user.query

import net.critika.infrastructure.validation.constraints.ValidUsername
import kotlinx.serialization.Serializable

@Serializable
data class UserNameQuery(
    @ValidUsername val username: String
)
