package net.critika.unit.usecase.user

import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.user.command.UserRatingCommand
import net.critika.application.user.command.UserRoleStatisticsCommand
import net.critika.application.user.usecase.UserRatingUseCase
import net.critika.application.user.usecase.UserRoleStatisticUseCase
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepositoryPort
import net.critika.domain.user.repository.UserRoleStatisticRepositoryPort
import net.critika.helpers.Helpers.getMockUser
import net.critika.helpers.Helpers.getMockUserRating
import org.jetbrains.exposed.dao.id.EntityID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class UserStatisticsUseCaseTest {
    private lateinit var userRatingRepository: UserRatingRepositoryPort
    private lateinit var userRatingUseCase: UserRatingUseCase
    private lateinit var userRoleStatisticRepository: UserRoleStatisticRepositoryPort
    private lateinit var userRoleStatisticsUseCase: UserRoleStatisticUseCase

    @BeforeEach
    fun setUp() {
        userRatingRepository = mock()
        userRatingUseCase = UserRatingUseCase(userRatingRepository)
        userRoleStatisticRepository = mock()
        userRoleStatisticsUseCase = UserRoleStatisticUseCase(userRoleStatisticRepository)
    }

    @Test
    fun `create user rating`() = runBlocking {
        val userIdMock = UUID.generateUUID(Random)
        val user = getMockUser()
        val userRating = getMockUserRating()
        whenever(userRating.userId).doReturn(user)

        whenever(userRatingRepository.create(any())).thenReturn(userRating)

        val result = userRatingUseCase.create(UserRatingCommand.Create(userIdMock))

        assertEquals(userRating.toResponse(), result)
    }

    @Test
    fun `get user rating`() = runBlocking {
        val userRatingId = UUID.generateUUID(Random)
        val user = getMockUser()
        val userRating = getMockUserRating()
        whenever(userRating.userId).doReturn(user)

        whenever(userRatingRepository.get(any())).thenReturn(userRating)

        val result = userRatingUseCase.get(userRatingId)

        assertEquals(userRating.toResponse(), result)
    }

    @Test
    fun `update user rating`(): Unit = runBlocking {
        val userRating = mock<UserRating>().apply {
            this.bonusPoints = 130
            this.bestMovePoints = 10
            this.totalPoints = 100
            this.malusPoints = 40
        }

        userRatingUseCase.update(UserRatingCommand.Update(userRating))

        verify(userRatingRepository).update(UserRatingCommand.Update(userRating))
    }

    @Test
    fun `delete user rating`(): Unit = runBlocking {
        val userId = UUID.generateUUID(Random)

        userRatingUseCase.delete(userId)

        verify(userRatingRepository).delete(userId)
    }

    @Test
    fun `create role statistic`() = runBlocking {
        val userId = UUID.generateUUID(Random)
        val userRatingId = UUID.generateUUID(Random)
        val userRating = getMockUserRating()
        val roleStatistic = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.CITIZEN
            on { gamesWon } doReturn 0
            on { gamesTotal } doReturn 0
            on { bonusPoints } doReturn 0
        }

        whenever(userRoleStatisticRepository.create(any())).thenReturn(roleStatistic)

        val result = userRoleStatisticsUseCase.create(UserRoleStatisticsCommand.Create(userId, userRatingId, PlayerRole.CITIZEN.name))

        assertEquals(roleStatistic, result)
    }

    @Test
    fun `get role statistics by user rating id`() = runBlocking {
        val userRatingId = UUID.generateUUID(Random)
        val userRating = getMockUserRating()
        val roleStatistic1 = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.CITIZEN
            on { gamesWon } doReturn 5
            on { gamesTotal } doReturn 10
            on { bonusPoints } doReturn 100
        }
        val roleStatistic2 = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.MAFIA
            on { gamesWon } doReturn 5
            on { gamesTotal } doReturn 10
            on { bonusPoints } doReturn 100
        }
        val roleStatistics = listOf(roleStatistic1, roleStatistic2)

        whenever(userRoleStatisticRepository.findByUserRating(any())).thenReturn(roleStatistics)

        val result = userRoleStatisticsUseCase.findByUserRatingId(userRatingId)

        assertEquals(roleStatistics, result)
    }

    @Test
    fun `update role statistic`(): Unit = runBlocking {
        val userRating = getMockUserRating()
        val roleStatistic = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.MAFIA
            on { gamesWon } doReturn 5
            on { gamesTotal } doReturn 10
            on { bonusPoints } doReturn 100
        }

        userRoleStatisticsUseCase.update(UserRoleStatisticsCommand.Update(roleStatistic))

        verify(userRatingRepository).update(any())
    }

    @Test
    fun `delete role statistic`(): Unit = runBlocking {
        val roleStatisticId = UUID.generateUUID(Random)

        userRoleStatisticsUseCase.delete(roleStatisticId)

        verify(userRatingRepository).delete(roleStatisticId)
    }
}
