package net.critika.application.stage.query

import kotlinx.uuid.UUID
import net.critika.infrastructure.validation.constraints.ValidUUID

data class StageQuery(
    @ValidUUID val id: UUID,
)
