package net.critika.story.game.steps

import io.qameta.allure.Step
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.application.game.usecase.GameUseCase
import net.critika.application.player.usecase.PlayerUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.uuid.UUID

class GameSteps : KoinComponent {
    private val gameUseCase: GameUseCase by inject()
    private val playerUseCase: PlayerUseCase by inject()

    @Step("Join game {gameId} by user {userId}")
    suspend fun joinGame(userId: UUID, gameId: UUID) {
        playerUseCase.enterGame(userId, gameId)
    }

    @Step("Start game {gameId}")
    suspend fun startGame(gameId: UUID) {
        gameUseCase.start(gameId)
    }

    @Step("Finish game {gameId} with winning role {winningRole}")
    suspend fun finish(gameId: UUID, winningRole: PlayerRole): Map<PlayerRole, MutableList<Player>> {
        return gameUseCase.finish(gameId, winningRole)
    }
}
