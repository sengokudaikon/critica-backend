package net.critika.application.tournament.query

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
data class TournamentQuery(
    val id: UUID,
)
