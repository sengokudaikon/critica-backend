package net.critika.story.game.steps

import io.qameta.allure.Step
import net.critika.domain.Player
import net.critika.domain.PlayerRole
import net.critika.usecase.game.GameUseCase
import net.critika.usecase.player.PlayerUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

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
