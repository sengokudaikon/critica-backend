package net.critika.unit.usecase.lobby

import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.lobby.response.LobbyResponse
import net.critika.application.lobby.usecase.LobbyCrudUseCase
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.unit.Helpers.getMockLobby
import net.critika.unit.Helpers.getMockUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.random.Random

class LobbyCrudUseCaseTest {
    private lateinit var lobbyRepository: LobbyRepository
    private lateinit var lobbyCrudUseCase: LobbyCrudUseCase

    @BeforeEach
    fun setUp() {
        lobbyRepository = mock()
        lobbyCrudUseCase = LobbyCrudUseCase(lobbyRepository)
    }

    @Test
    fun `create lobby`() = runBlocking {
        val clubId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val user = getMockUser()
        val dateM = mock<LocalDateTime>().toString()
        val lobby = getMockLobby()
        whenever(lobby.creator).thenReturn(user)

        val lobbyResponse = LobbyResponse(
            lobbyId.toString(),
            dateM,
            user.id.toString(),
        )

        whenever(lobbyRepository.create(any())).thenReturn(lobby)

        val result = lobbyCrudUseCase.create(LobbyCommand.Create(user.id.value, dateM, clubId))

        assertEquals(lobbyResponse, result)
    }

    @Test
    fun `get lobby`() = runBlocking {
        val lobbyId = UUID.generateUUID(Random)
        val lobby = getMockLobby()
        val user = getMockUser()
        whenever(lobby.creator).thenReturn(user)
        val date = mock<LocalDateTime>().toString()
        val lobbyResponse = LobbyResponse(
            lobbyId.toString(),
            date,
            user.id.toString(),
        )

        whenever(lobbyRepository.get(any())).thenReturn(lobby)

        val result = lobbyCrudUseCase.get(lobbyId)

        assertEquals(lobbyResponse, result)
    }

    @Test
    fun `list lobbies`() = runBlocking {
        val lobbyId = UUID.generateUUID(Random)
        val lobby = getMockLobby()
        val lobby2 = getMockLobby()
        val user = getMockUser()
        whenever(lobby.creator).thenReturn(user)
        val date = mock<LocalDateTime>().toString()
        val date2 = Mockito.mock<LocalDateTime>().toString()
        val lobbyResponse1 = LobbyResponse(
            lobbyId.toString(),
            date,
            user.id.toString(),
        )
        val lobbyResponse2 =
            LobbyResponse(
                UUID.generateUUID(Random).toString(),
                date2,
                user.id.toString(),
            )
        val lobbies = listOf(lobby, lobby2)

        whenever(lobbyRepository.list()).thenReturn(lobbies)
        val responseList = listOf(lobbyResponse1, lobbyResponse2)
        val result = lobbyCrudUseCase.list()
        assertEquals(responseList, result)
    }

    @Test
    fun `delete lobby`() = runBlocking {
        val lobbyId = UUID.generateUUID(Random)

        lobbyCrudUseCase.delete(lobbyId)

        verify(lobbyRepository).delete(lobbyId)
    }
}
