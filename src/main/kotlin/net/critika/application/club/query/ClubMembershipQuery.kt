package net.critika.application.club.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
data class ClubMembershipQuery(
    val userId: UUID
)
