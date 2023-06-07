package net.critika.unit.usecase.player

import arrow.core.Either
import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.game.command.GameCommand
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.player.command.PlayerCommand
import net.critika.application.player.usecase.PlayerUseCase
import net.critika.infrastructure.exception.LobbyException
import net.critika.persistence.club.repository.GameRepository
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.persistence.club.repository.PlayerRepository
import net.critika.unit.Helpers.getMockGame
import net.critika.unit.Helpers.getMockLobby
import net.critika.unit.Helpers.getMockPlayer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random
import kotlin.test.assertEquals

class PlayerUseCaseTest {

    private val playerRepository: PlayerRepository = mock()
    private val lobbyRepository: LobbyRepository = mock()
    private val gameRepository: GameRepository = mock()

    private lateinit var playerUseCase: PlayerUseCase

    @BeforeEach
    fun setUp() {
        playerUseCase = PlayerUseCase(playerRepository, lobbyRepository, gameRepository)
    }

    @Test
    fun `getPlayerProfile by name success`() = runBlocking {
        // Given
        val playerName = "TestPlayer"
        val player = getMockPlayer().apply { name = playerName }
        whenever(playerRepository.getPlayerByName(playerName)).thenReturn(player)

        // When
        val result = playerUseCase.getPlayerProfile(playerName)

        // Then
        verify(playerRepository, times(1)).getPlayerByName(playerName)
        assertEquals(Either.Right(player), result)
    }

    @Test
    fun `getPlayerProfile by id success`() = runBlocking {
        // Given
        val playerId = UUID.generateUUID(Random)
        val player = getMockPlayer()
        whenever(playerRepository.get(playerId)).thenReturn(player)

        // When
        val result = playerUseCase.getPlayerProfile(playerId)

        // Then
        verify(playerRepository, times(1)).get(playerId)
        assertEquals(player, result)
    }

    @Test
    fun `enterLobby success`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.lobby = null }
        val lobby = getMockLobby()
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)
        whenever(lobbyRepository.get(lobbyId)).thenReturn(lobby)

        // When
        val result = playerUseCase.enterLobby(userId, lobbyId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(lobbyRepository, times(1)).get(lobbyId)
        verify(playerRepository, times(1)).update(PlayerCommand.Save(player))
        verify(lobbyRepository, times(1)).update(LobbyCommand.Save(lobby))
        assertEquals(Either.Right(lobby.toResponse()), result)
    }

    @Test
    fun `enterLobby player already in lobby`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.lobby = getMockLobby() }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.enterLobby(userId, lobbyId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        assertEquals(Either.Left(LobbyException.AlreadyInLobby("Player is already in a lobby")), result)
    }

    @Test
    fun `enterLobby lobby not found`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.lobby = null }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)
        whenever(lobbyRepository.get(lobbyId)).thenReturn(null)

        // When
        val result = playerUseCase.enterLobby(userId, lobbyId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(lobbyRepository, times(1)).get(lobbyId)
        assertEquals(Either.Left(LobbyException.NotFound("Lobby not found")), result)
    }

    @Test
    fun `enterGame success`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val gameId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.game = null }
        val game = getMockGame()
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)
        whenever(gameRepository.get(gameId)).thenReturn(game)

        // When
        val result = playerUseCase.enterGame(userId, gameId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(gameRepository, times(1)).get(gameId)
        verify(playerRepository, times(1)).update(PlayerCommand.Save(player))
        verify(gameRepository, times(1)).update(GameCommand.Save(game))
        assertEquals(Either.Right(game.toResponse()), result)
    }

    @Test
    fun `enterGame player already in game`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val gameId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.game = getMockGame() }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.enterGame(userId, gameId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        assertEquals(Either.Left(LobbyException.AlreadyInLobby("Player is already in a game")), result)
    }

    @Test
    fun `enterGame game not found`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val gameId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.game = null }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)
        whenever(gameRepository.get(gameId)).thenReturn(null)

        // When
        val result = playerUseCase.enterGame(userId, gameId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(gameRepository, times(1)).get(gameId)
        assertEquals(Either.Left(LobbyException.NotFound("Game not found")), result)
    }

    @Test
    fun `quitGame success`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.game = getMockGame() }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.quitGame(userId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(playerRepository, times(1)).update(PlayerCommand.Save(player))
        assertEquals(Either.Right(player), result)
    }

    @Test
    fun `quitGame player not in game`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.game = null }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.quitGame(userId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        assertEquals(Either.Left(LobbyException.NotFound("Player is not in a game")), result)
    }

    @Test
    fun `exitLobby success`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.lobby = getMockLobby() }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.exitLobby(userId, lobbyId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        verify(playerRepository, times(1)).update(PlayerCommand.Save(player))
        assertEquals(Either.Right(player), result)
    }

    @Test
    fun `exitLobby player not in lobby`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val lobbyId = UUID.generateUUID(Random)
        val player = getMockPlayer().apply { this.lobby = null }
        whenever(playerRepository.getPlayerByUserId(userId)).thenReturn(player)

        // When
        val result = playerUseCase.exitLobby(userId, lobbyId)

        // Then
        verify(playerRepository, times(1)).getPlayerByUserId(userId)
        assertEquals(Either.Left(LobbyException.NotFound("Player is not in this lobby")), result)
    }
}
