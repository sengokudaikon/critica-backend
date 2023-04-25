package io.critica.application.common.query

import io.critica.infrastructure.validation.ValidIsoDate
import kotlinx.serialization.Serializable

@Serializable
data class DateQuery(
    @field:ValidIsoDate
    val date: String
)
