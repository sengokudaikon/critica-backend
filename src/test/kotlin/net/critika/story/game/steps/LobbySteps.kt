package net.critika.story.game.steps

import io.qameta.allure.Step
import net.critika.application.lobby.response.LobbyResponse
import net.critika.usecase.lobby.LobbyCrudUseCase
import net.critika.usecase.lobby.LobbyUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class LobbySteps : KoinComponent {
    private val lobbyCrudUseCase: LobbyCrudUseCase by inject()
    private val lobbyUseCase: LobbyUseCase by inject()

    @Step("Create lobby by {creatorName}")
    suspend fun create(creatorName: UUID): LobbyResponse {
        return lobbyCrudUseCase.create(creatorName, LocalDateTime.now())
    }

    @Step("Add player {playerName} to lobby {lobbyId}")
    suspend fun addPlayer(lobbyId: UUID, playerName: String) {
        lobbyUseCase.addPlayer(lobbyId, playerName)
    }

    suspend fun addGame(id: String, s: String): LobbyResponse {
        return lobbyUseCase.addGame(UUID.fromString(id), LocalTime.now(), s).getOrNull()!!
    }
}
