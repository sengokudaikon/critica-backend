package net.critika.application.game.command

import kotlinx.uuid.UUID
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.GameStatus
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.User
import java.time.LocalDateTime

interface GameCommand {
    data class Create(
        val date: LocalDateTime,
        val host: UUID?,
        val status: GameStatus = GameStatus.CREATED,
    ) : GameCommand

    data class Update(
        val game: Game,
        val host: User? = null,
        val status: GameStatus? = null,
        val winner: PlayerRole? = null,
    ) : GameCommand

    data class Save(
        val game: Game,
    ) : GameCommand

    data class Delete(
        val id: UUID,
    ) : GameCommand
}
