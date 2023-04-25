package io.critica.application.lobby.command

import io.critica.infrastructure.validation.ValidIsoDate
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
data class CreateLobby(
    @field:ValidIsoDate
    val date: String,
    @field:NotBlank
    val name: String
)
