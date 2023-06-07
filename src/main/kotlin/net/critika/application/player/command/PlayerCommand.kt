package net.critika.application.player.command

import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User

interface PlayerCommand {
    data class Create(
        val user: User,
        val playerName: String,
        val lobbyId: UUID,
        val gameId: UUID?,
    ) : PlayerCommand

    data class Update(
        val id: UUID,
        val playerName: String,
    ) : PlayerCommand

    data class Delete(
        val id: UUID,
    ) : PlayerCommand

    data class Save(
        val player: Player,
    ) : PlayerCommand
}
