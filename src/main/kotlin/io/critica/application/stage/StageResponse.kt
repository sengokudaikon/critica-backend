package io.critica.application.stage

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DayStageResponse::class, name = "day"),
    JsonSubTypes.Type(value = NightStageResponse::class, name = "night")
)
sealed class StageResponse(val type: String)
