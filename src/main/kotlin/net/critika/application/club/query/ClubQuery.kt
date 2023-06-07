package net.critika.application.club.query

import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidUUID

data class ClubQuery(
    @ValidUUID val id: UUID,
)
