package net.critika.application.player.query

import kotlinx.serialization.Serializable
import net.critika.infrastructure.validation.constraints.ValidPlayerName

@Serializable
data class PlayerNameQuery(
    @ValidPlayerName val playerName: String,
)
