package net.critika.application.stage

import net.critika.infrastructure.validation.constraints.ValidUUID

data class StageQuery(
    @ValidUUID val id: String
)