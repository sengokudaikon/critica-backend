package net.critika.unit.usecase.stage

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.qameta.allure.Description
import io.qameta.allure.Feature
import kotlinx.coroutines.runBlocking
import net.critika.application.stage.DayStageResponse
import net.critika.domain.*
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.model.DayStage
import net.critika.domain.events.model.DayVote
import net.critika.domain.events.repository.EventRepository
import net.critika.domain.user.model.User
import net.critika.persistence.db.Games
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.PlayerRepository
import net.critika.unit.Helpers.getMockGame
import net.critika.unit.Helpers.getMockLobby
import net.critika.unit.Helpers.getMockPlayer
import net.critika.unit.Helpers.getMockUser
import net.critika.usecase.stage.StageUseCase
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.emptySized
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*

class NextStageUseCaseTest {

    @Mock
    private var gameRepository: GameRepository = mock()

    @Mock
    private var playerRepository: PlayerRepository = mock()

    private var eventRepository: EventRepository = mock()

    private lateinit var stageUseCase: StageUseCase

    private lateinit var mockGame: Game
    private lateinit var mockLobby: Lobby
    private lateinit var mockPlayer: Player
    private lateinit var mockUser: User

    @BeforeEach
    fun setUp() {
        // Set up mock data and responses
        mockGame = getMockGame()
        mockLobby = getMockLobby()
        mockPlayer = getMockPlayer()
        mockUser = getMockUser()
        stageUseCase = StageUseCase(eventRepository, playerRepository, gameRepository)
    }

    @Feature("Next Stage")
    @Description("DayEvent.DISCUSS goes to the next stage VOTE")
    @Test
    fun `nextStage success`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        val stageId = UUID.randomUUID()
        val stageEntityID = mock<EntityID<UUID>> {
            on { value } doReturn stageId
        }
        val dayEvent = mock<DayEvent> {
            on { id } doReturn stageEntityID
            on { game } doReturn EntityID(gameId, Games)
            on { day } doReturn 1
            on { stage } doReturn DayStage.DISCUSS
        }

        val eventsList = SizedCollection(dayEvent)
        `when`(mockGame.dayEvents).thenAnswer {
            eventsList
        }
        `when`(gameRepository.get(gameId)).thenReturn(mockGame)
        `when`(eventRepository.findStage(stageId)).thenReturn(dayEvent)
        `when`(mockGame.getCurrentStage()).thenReturn(dayEvent)

        // When
        val dayResult = stageUseCase.nextStage(gameId, stageId)

        // Then
        assertTrue(dayResult.currentStage is DayStageResponse)
        assertEquals(DayStage.VOTE, (dayResult.currentStage as DayStageResponse).stage)
        verify(gameRepository, times(1)).get(gameId)
        verify(eventRepository, times(1)).save(dayEvent)
    }

    @Feature("Next Stage")
    @Description("Test when the current stage is DayStage.VOTE ends with player elimination")
    @Test
    fun `dayStageVote goes to end`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        val stageId = UUID.randomUUID()

        val currentStage = mock<DayEvent> {
            on { id.value } doReturn stageId
            on { game } doReturn mockGame.id
            on { day } doReturn 1
            on { stage } doReturn DayStage.VOTE
        }

        val targetPlayer = getMockPlayer()
        val voterPlayer = getMockPlayer()

        val dayVote = mock<DayVote> {
            on { id.value } doReturn UUID.randomUUID()
            on { day } doReturn currentStage
            on { voter } doReturn voterPlayer
            on { target } doReturn targetPlayer
        }
        currentStage.votes.shouldBe(emptySized())
        `when`(currentStage.votes).thenAnswer {
            currentStage.votes.plus(dayVote)
            currentStage.votes
        }
        currentStage.votes.shouldContain(dayVote)
        `when`(mockGame.getCurrentStage()).thenReturn(currentStage)
        `when`(gameRepository.get(gameId)).thenReturn(mockGame)

        // When
        val result = stageUseCase.nextStage(gameId, stageId)

        // Then
        assertTrue(result.currentStage is DayStageResponse)
        assertEquals(DayStage.END, (result.currentStage as DayStageResponse).stage)
        verify(gameRepository, times(1)).get(gameId)
        verify(eventRepository, times(1)).save(currentStage)
    }

    @Feature("Next Stage")
    @Description("Test when the current stage is DayStage.VOTE goes to REDISCUSS")
    @Test
    fun `dayStageVote goes to rediscuss`(): Unit = runBlocking {
        // Given
        val gameId = UUID.randomUUID()
        val stageId = UUID.randomUUID()

        val currentStage = mock<DayEvent> {
            on { id.value } doReturn stageId
            on { game } doReturn mockGame.id
            on { day } doReturn 1
            on { stage } doReturn DayStage.VOTE
        }

        val targetPlayer = getMockPlayer()
        val voterPlayer = getMockPlayer()

        val dayVote = mock<DayVote> {
            on { id.value } doReturn UUID.randomUUID()
            on { day } doReturn currentStage
            on { voter } doReturn voterPlayer
            on { target } doReturn targetPlayer
        }

        val targetPlayer2 = getMockPlayer()
        val voterPlayer2 = getMockPlayer()

        val dayVote2 = mock<DayVote> {
            on { id.value } doReturn UUID.randomUUID()
            on { day } doReturn currentStage
            on { voter } doReturn voterPlayer2
            on { target } doReturn targetPlayer2
        }
        currentStage.votes.shouldBe(emptySized())
        `when`(currentStage.votes).thenAnswer {
            currentStage.votes.plus(dayVote)
            currentStage.votes.plus(dayVote2)
            currentStage.votes
        }
        currentStage.votes.shouldContain(dayVote)
        currentStage.votes.shouldContain(dayVote2)

        `when`(mockGame.getCurrentStage()).thenReturn(currentStage)
        `when`(gameRepository.get(gameId)).thenReturn(mockGame)

        // When
        val result = stageUseCase.nextStage(gameId, stageId)

        // Then
        assertTrue(result.currentStage is DayStageResponse)
        assertEquals(DayStage.REDISCUSS, (result.currentStage as DayStageResponse).stage)
        verify(gameRepository, times(1)).get(gameId)
        verify(eventRepository, times(1)).save(currentStage)
    }
}
