package net.critika.application.user.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
data class UserQuery(
    val id: UUID,
)
