package net.critika.unit.usecase.game

import io.qameta.allure.Description
import io.qameta.allure.Feature
import kotlinx.coroutines.runBlocking
import net.critika.domain.*
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepository
import net.critika.persistence.db.UserSettings
import net.critika.persistence.db.Users
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.LobbyRepository
import net.critika.persistence.repository.PlayerRepository
import net.critika.unit.Helpers.getMockGame
import net.critika.unit.Helpers.getMockLobby
import net.critika.unit.Helpers.getMockPlayer
import net.critika.unit.Helpers.getMockUser
import net.critika.usecase.game.GameUseCase
import net.critika.usecase.user.UserStatisticsUseCase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testng.annotations.BeforeTest
import java.util.*

@Testcontainers
class GameUseCaseTest {

    @Mock
    private var repository: GameRepository = mock()

    @Mock
    private var lobbyRepository: LobbyRepository = mock()

    @Mock
    private var playerRepository: PlayerRepository = mock()

    @Mock
    private var userStatisticsUseCase: UserStatisticsUseCase = mock()
    private var userRepository: UserRepository = mock()

    private lateinit var gameUseCase: GameUseCase

    private lateinit var mockGame: Game
    private lateinit var mockLobby: Lobby
    private lateinit var mockPlayer: Player
    private lateinit var mockUser: User

    companion object {
        @Container
        val postgreSQLContainer = PostgreSQLContainer<Nothing>(
            DockerImageName.parse("postgres:13.3"),
        ).apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_password")
        }
    }

    @BeforeTest
    fun configure() {
        Database.connect(
            url = postgreSQLContainer.jdbcUrl,
            driver = postgreSQLContainer.driverClassName,
            user = postgreSQLContainer.username,
            password = postgreSQLContainer.password,
        )
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, UserSettings)
        }
    }

    @BeforeEach
    fun setUp() {
        // Set up mock data and responses
        mockGame = getMockGame()
        mockLobby = getMockLobby()
        mockPlayer = getMockPlayer()
        mockUser = getMockUser()
        gameUseCase = GameUseCase(repository, lobbyRepository, playerRepository, userRepository, userStatisticsUseCase)
    }

    @Feature("Assign Host")
    @Description("Assign a host to a game")
    @Test
    fun `assignHost success`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        val hostId = UUID.randomUUID()
        val mockUser = getMockUser()
        `when`(mockUser.role).thenReturn(UserRole.HOST)
        `when`(userRepository.findById(any())).thenReturn(mockUser)
        `when`(repository.get(gameId)).thenReturn(mockGame)

        // When
        gameUseCase.assignHost(gameId, hostId)

        // Then
        Mockito.verify(repository, Mockito.times(1)).get(gameId)
        Mockito.verify(repository, Mockito.times(1)).update(mockGame, GameStatus.WAITING)
    }

    @Feature("Get Game")
    @Description("Get a game by ID")
    @Test
    fun `get success`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        `when`(mockGame.id.value).thenReturn(gameId)
        `when`(repository.get(gameId)).thenReturn(mockGame)

        // When
        val gameResponse = gameUseCase.get(gameId)

        // Then
        assertNotNull(gameResponse)
        Mockito.verify(repository, Mockito.times(1)).get(gameId)
    }

    @Feature("List Games")
    @Description("List all games")
    @Test
    fun `list success`(): Unit = runBlocking {
        // Given
        val games = listOf(mockGame)
        `when`(repository.getGames()).thenReturn(games)

        // When
        val result = gameUseCase.list()

        // Then
        assertEquals(games, result)
        Mockito.verify(repository, Mockito.times(1)).getGames()
    }

    @Feature("Start Game")
    @Description("Start a game")
    @Test
    fun `start success`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        `when`(mockGame.lobby).thenReturn(mockLobby)
        `when`(repository.get(gameId)).thenReturn(mockGame)

        // When
        val result = gameUseCase.start(gameId)

        // Then
        assertTrue(result.isRight())
        Mockito.verify(repository, Mockito.times(1)).get(gameId)
        Mockito.verify(repository, Mockito.times(1)).update(mockGame, GameStatus.STARTED)
    }

    @Feature("Finish Game")
    @Description("Finish a game")
    @Test
    fun `finish success`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        val winningTeam = PlayerRole.MAFIA
        `when`(mockGame.lobby).thenReturn(mockLobby)
        `when`(repository.get(gameId)).thenReturn(mockGame)

        // When
        val resultMap = gameUseCase.finish(gameId, winningTeam)

        // Then
        assertTrue(resultMap.containsKey(winningTeam))
        Mockito.verify(repository, Mockito.times(1)).get(gameId)
        Mockito.verify(repository, Mockito.times(1)).update(mockGame, GameStatus.FINISHED, winningTeam)
    }
}
