package io.critica.application.common.query

import kotlinx.serialization.Serializable

@Serializable
data class DateQuery(
    val date: String
)
