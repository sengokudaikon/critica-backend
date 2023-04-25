package io.critica.application.player

import io.critica.domain.PlayerRole
import io.critica.domain.PlayerStatus
import java.util.UUID

data class PlayerResponse(
    val id: UUID,
    val seat: Int,
    val name: String,
    val role: PlayerRole,
    val status: PlayerStatus,
    val inGame: Boolean,
    val bonusPoints: Int,
)
