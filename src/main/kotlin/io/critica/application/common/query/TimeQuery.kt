package io.critica.application.common.query

import kotlinx.serialization.Serializable

@Serializable
data class TimeQuery(
    val time: String,
)