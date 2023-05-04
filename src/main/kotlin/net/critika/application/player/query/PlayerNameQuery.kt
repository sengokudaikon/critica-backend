package net.critika.application.player.query

import net.critika.infrastructure.validation.constraints.ValidPlayerName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerNameQuery(
    @ValidPlayerName val playerName: String
)
