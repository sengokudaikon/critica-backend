package net.critika.application.user.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidUsername

@Serializable
data class UserNameQuery(
    @ValidUsername val username: String,
)
