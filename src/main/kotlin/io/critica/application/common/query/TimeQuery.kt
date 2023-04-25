package io.critica.application.common.query

import io.critica.infrastructure.validation.ValidTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeQuery(
    @field:ValidTime
    val time: String,
)