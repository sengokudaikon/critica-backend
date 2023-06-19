package net.critika.application.stage.response

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlinx.serialization.Serializable
import net.critika.application.Response

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DayStageResponse::class, name = "day"),
    JsonSubTypes.Type(value = NightStageResponse::class, name = "night"),
)
@Serializable
sealed class StageResponse : Response
