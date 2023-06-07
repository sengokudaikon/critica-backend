package net.critika.story.game.steps

import io.qameta.allure.Step
import kotlinx.uuid.UUID
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.lobby.usecase.LobbyCrudUseCase
import net.critika.application.lobby.usecase.LobbyUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.LocalTime

class LobbySteps : KoinComponent {
    private val lobbyCrudUseCase: LobbyCrudUseCase by inject()
    private val lobbyUseCase: LobbyUseCase by inject()

    @Step("Create lobby by {creatorName}")
    suspend fun create(creatorName: UUID, clubId: UUID): LobbyResponse {
        return lobbyCrudUseCase.create(LobbyCommand.Create(creatorName, date = LocalDateTime.now().toString(), clubId))
    }

    @Step("Add player {playerName} to lobby {lobbyId}")
    suspend fun addPlayer(lobbyId: UUID, playerName: String) {
        lobbyUseCase.addPlayer(lobbyId, playerName)
    }

    suspend fun addGame(id: UUID, s: UUID): LobbyResponse {
        return lobbyUseCase.addGame(id, LocalTime.now(), s).getOrNull()!!
    }
}
