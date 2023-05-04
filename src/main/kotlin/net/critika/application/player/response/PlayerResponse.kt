package net.critika.application.player.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.critika.domain.PlayerRole
import net.critika.domain.PlayerStatus
import java.util.UUID

@Serializable
data class PlayerResponse(
    @Contextual val id: UUID,
    val seat: Int,
    val name: String,
    val role: PlayerRole?,
    val status: PlayerStatus,
    val inGame: Boolean,
    val bestMove: Int,
    val bonusPoints: Int,
)
