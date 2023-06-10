package net.critika.unit.usecase.lobby

import arrow.core.Either
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.lobby.usecase.LobbyUseCase
import net.critika.application.player.command.PlayerCommand
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.GameStatus
import net.critika.domain.club.model.Lobby
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.helpers.Helpers.getMockGame
import net.critika.helpers.Helpers.getMockLobby
import net.critika.helpers.Helpers.getMockLobbyWithPlayer
import net.critika.helpers.Helpers.getMockPlayer
import net.critika.helpers.Helpers.getMockUser
import net.critika.persistence.club.repository.GameRepository
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.persistence.club.repository.PlayerRepository
import net.critika.persistence.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalTime
import kotlin.random.Random

class LobbyUseCaseTest {

    private lateinit var lobbyRepository: LobbyRepository
    private lateinit var userRepository: UserRepository
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
        val id = UUID.generateUUID(Random)
        val time = LocalTime.now()
        val host = UUID.generateUUID(Random)
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
        val id = UUID.generateUUID(Random)
        val gameId = UUID.generateUUID(Random)
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
        val id = UUID.generateUUID(Random)
        val playerName = "TestPlayer"
        val lobby = getMockLobby()
        val user = getMockUser()
        val player = getMockPlayer().apply { name = playerName }
        `when`(lobbyRepository.get(id)).thenAnswer {
            lobby.players.shouldBeEmpty()
            lobby
        }
        whenever(userRepository.findByPlayerName(playerName)).thenReturn(user)
        whenever(playerRepository.create(PlayerCommand.Create(any(), any(), any(), any()))).thenReturn(player)

        // When
        val result = lobbyUseCase.addPlayer(id, playerName)

        // Then
        verify(lobbyRepository, times(1)).get(id)
        verify(userRepository, times(1)).findByPlayerName(playerName)
        verify(playerRepository, times(1)).create((PlayerCommand.Create(user, "Test player", lobby.id.value, null)))
        result.shouldBe(Either.Right(lobby.toResponse()))
        result.getOrNull()?.players?.contains(player.toResponse())?.shouldBe(true)
    }

    @Test
    fun `addTemporaryPlayer success`(): Unit = runBlocking {
        // Given
        val id = UUID.generateUUID(Random)
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
        val id = UUID.generateUUID(Random)
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
        val id = UUID.generateUUID(Random)
        val playerId = UUID.generateUUID(Random)
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
