package net.critika.application.player.response

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.gameprocess.model.PlayerStatus

@Serializable
data class PlayerResponse(
    val id: UUID,
    val seat: Int,
    val name: String,
    var role: PlayerRole?,
    val status: PlayerStatus,
    val inGame: Boolean,
    val bestMove: Int,
    val bonusPoints: Int,
)
