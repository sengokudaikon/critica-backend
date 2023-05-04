package net.critika.unit.usecase.lobby

import arrow.core.Either
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import net.critika.domain.Game
import net.critika.domain.GameStatus
import net.critika.domain.Lobby
import net.critika.domain.Player
import net.critika.domain.user.model.User
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.LobbyRepository
import net.critika.persistence.repository.PlayerRepository
import net.critika.persistence.repository.UserRepositoryImpl
import net.critika.unit.Helpers.getMockGame
import net.critika.unit.Helpers.getMockLobby
import net.critika.unit.Helpers.getMockLobbyWithPlayer
import net.critika.unit.Helpers.getMockPlayer
import net.critika.unit.Helpers.getMockUser
import net.critika.usecase.lobby.LobbyUseCase
import org.joda.time.LocalTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class LobbyUseCaseTest {

    private lateinit var lobbyRepository: LobbyRepository
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var gameRepository: GameRepository
    private lateinit var playerRepository: PlayerRepository
    private lateinit var lobbyUseCase: LobbyUseCase

    private lateinit var mockLobby: Lobby
    private lateinit var mockGame: Game
    private lateinit var mockUser: User
    private lateinit var mockPlayer: Player

    @BeforeEach
    fun setUp() {
        lobbyRepository = mock()
        userRepository = mock()
        gameRepository = mock()
        playerRepository = mock()

        lobbyUseCase = LobbyUseCase(lobbyRepository, userRepository, gameRepository, playerRepository)

        // Set up mock data and responses
        mockGame = getMockGame()
        mockLobby = getMockLobby()
        mockUser = getMockUser()
        mockPlayer = getMockPlayer()
    }

    @Test
    fun `addGame success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val time = LocalTime.now()
        val host = "Test Host"
        val lobby = getMockLobby()
        val game = getMockGame()
        whenever(lobby.creator).thenReturn(mockUser)
        `when`(lobbyRepository.get(id)).thenAnswer {
            lobby.games.shouldBeEmpty()
            lobby
        }
        `when`(gameRepository.create(any())).thenReturn(game)

        // When
        val result = lobbyUseCase.addGame(id, time, host)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        verify(gameRepository, times(1)).create(any())
        result.shouldBe(Either.Right(game.toResponse()))
        val lobbyResponse = result.getOrNull()
        lobbyResponse?.games?.contains(game.toResponse())?.shouldBe(true)
    }

    @Test
    fun `removeGame success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        val lobby = getMockLobby()
        val game = getMockGame().apply { status = GameStatus.WAITING }
        lobby.apply { games.plus(game) }
        whenever(lobbyRepository.get(id)).thenReturn(lobby)
        whenever(gameRepository.get(gameId)).thenReturn(game)

        // When
        val result = lobbyUseCase.removeGame(id, gameId)

        // Then
        verify(lobbyRepository).get(id)
        verify(gameRepository).get(gameId)
        result.shouldBe(Either.Right(lobby.toResponse()))
        result.getOrNull()?.games?.contains(game.toResponse())?.shouldBe(false)
    }

    @Test
    fun `addPlayer success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val playerName = "TestPlayer"
        val lobby = getMockLobby()
        val user = getMockUser().apply { username = playerName }
        val player = getMockPlayer().apply { name = playerName }
        `when`(lobbyRepository.get(id)).thenAnswer {
            lobby.players.shouldBeEmpty()
            lobby
        }
        whenever(userRepository.findByUsername(playerName)).thenReturn(user)
        whenever(playerRepository.create(user)).thenReturn(player)

        // When
        val result = lobbyUseCase.addPlayer(id, playerName)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        verify(userRepository, times(1)).findByUsername(playerName)
        verify(playerRepository, times(1)).create(user)
        result.shouldBe(Either.Right(lobby.toResponse()))
        result.getOrNull()?.players?.contains(player.toResponse())?.shouldBe(true)
    }

    @Test
    fun `addTemporaryPlayer success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val playerName = "TestPlayer"
        val lobby = getMockLobby()
        val tempPlayer = getMockPlayer().apply { name = playerName }
        `when`(lobbyRepository.get(id)).thenAnswer {
            lobby.players.shouldBeEmpty()
            lobby
        }
        whenever(playerRepository.createTemporaryPlayer(playerName, id, null)).thenReturn(tempPlayer)

        // When
        val result = lobbyUseCase.addTemporaryPlayer(id, playerName)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        verify(playerRepository, times(1)).createTemporaryPlayer(playerName, id, null)
        result.shouldBe(Either.Right(lobby.toResponse()))
        result.getOrNull()?.players?.contains(tempPlayer.toResponse())?.shouldBe(true)
    }

    @Test
    fun `removePlayer success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val playerName = "TestPlayer"
        val lobby = getMockLobby()
        val player = getMockPlayer().apply { name = playerName }
        `when`(lobbyRepository.get(id)).thenAnswer {
            lobby.players.plus(player)
            lobby
        }
        `when`(playerRepository.getPlayerByName(playerName)).thenReturn(player)

        // When
        val result = lobbyUseCase.removePlayer(id, playerName)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        result.shouldBe(Either.Right(lobby.toResponse()))
        result.getOrNull()?.players?.contains(player.toResponse())?.shouldBe(false)
    }

    @Test
    fun `removePlayerById success`(): Unit = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val lobby = getMockLobbyWithPlayer()
        val player = getMockPlayer()
        whenever(lobbyRepository.get(id)).thenReturn(lobby)
        whenever(playerRepository.get(playerId)).thenReturn(player)

        // When
        val result = lobbyUseCase.removePlayerById(id, playerId)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        verify(playerRepository, times(1)).get(playerId)
        result.shouldBe(Either.Right(lobby.toResponse()))
        val lobbyResponse = result.getOrNull()
        lobbyResponse?.players?.contains(player.toResponse())?.shouldBe(false)
    }
}
