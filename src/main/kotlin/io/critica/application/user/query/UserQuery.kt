package io.critica.application.user.query

import io.critica.infrastructure.validation.ValidUUID
import kotlinx.serialization.Serializable

@Serializable
data class UserQuery(
    @field:ValidUUID
    val id: String
)
