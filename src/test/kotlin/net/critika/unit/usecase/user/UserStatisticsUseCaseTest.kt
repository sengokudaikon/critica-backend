package net.critika.unit.usecase.user

import kotlinx.coroutines.runBlocking
import net.critika.domain.PlayerRole
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.repository.UserRatingRepository
import net.critika.unit.Helpers.getMockUser
import net.critika.unit.Helpers.getMockUserRating
import net.critika.usecase.user.UserStatisticsUseCase
import org.jetbrains.exposed.dao.id.EntityID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class UserStatisticsUseCaseTest {
    private lateinit var userRatingRepository: UserRatingRepository
    private lateinit var userStatisticsUseCase: UserStatisticsUseCase

    @BeforeEach
    fun setUp() {
        userRatingRepository = mock()
        userStatisticsUseCase = UserStatisticsUseCase(userRatingRepository)
    }

    @Test
    fun `create user rating`() = runBlocking {
        val userIdMock = UUID.randomUUID()
        val user = getMockUser()
        val userRating = getMockUserRating()
        whenever(userRating.userId).doReturn(user)

        whenever(userRatingRepository.createUserRating(any())).thenReturn(userRating)

        val result = userStatisticsUseCase.createUserRating(userIdMock)

        assertEquals(userRating, result)
    }

    @Test
    fun `get user rating`() = runBlocking {
        val userRatingId = UUID.randomUUID()
        val user = getMockUser()
        val userRating = getMockUserRating()
        whenever(userRating.userId).doReturn(user)

        whenever(userRatingRepository.findUserRatingById(any())).thenReturn(userRating)

        val result = userStatisticsUseCase.getUserRating(userRatingId)

        assertEquals(userRating, result)
    }

    @Test
    fun `update user rating`(): Unit = runBlocking {
        val userId = UUID.randomUUID()
        val userRating = mock<UserRating>().apply {
            this.bonusPoints = 130
            this.bestMovePoints = 10
            this.totalPoints = 100
            this.malusPoints = 40
        }

        userStatisticsUseCase.updateUserRating(userRating)

        verify(userRatingRepository).updateUserRating(userRating)
    }

    @Test
    fun `delete user rating`(): Unit = runBlocking {
        val userId = UUID.randomUUID()

        userStatisticsUseCase.deleteUserRating(userId)

        verify(userRatingRepository).deleteUserRating(userId)
    }

    @Test
    fun `create role statistic`() = runBlocking {
        val userRatingId = UUID.randomUUID()
        val userRating = getMockUserRating()
        val roleStatistic = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.CITIZEN
            on { gamesWon } doReturn 0
            on { gamesTotal } doReturn 0
            on { bonusPoints } doReturn 0
        }

        whenever(userRatingRepository.createRoleStatistic(any(), any())).thenReturn(roleStatistic)

        val result = userStatisticsUseCase.createRoleStatistic(userRatingId, PlayerRole.CITIZEN)

        assertEquals(roleStatistic, result)
    }

    @Test
    fun `get role statistics by user rating id`() = runBlocking {
        val userRatingId = UUID.randomUUID()
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

        whenever(userRatingRepository.findRoleStatisticsByUserRatingId(any())).thenReturn(roleStatistics)

        val result = userStatisticsUseCase.getRoleStatisticsByUserRatingId(userRatingId)

        assertEquals(roleStatistics, result)
    }

    @Test
    fun `update role statistic`(): Unit = runBlocking {
        val roleStatisticId = UUID.randomUUID()
        val userRatingId = UUID.randomUUID()
        val userRating = getMockUserRating()
        val roleStatistic = mock<RoleStatistic> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { it.userRatingId } doReturn userRating
            on { role } doReturn PlayerRole.MAFIA
            on { gamesWon } doReturn 5
            on { gamesTotal } doReturn 10
            on { bonusPoints } doReturn 100
        }

        userStatisticsUseCase.updateRoleStatistic(roleStatistic)

        verify(userRatingRepository).updateRoleStatistic(roleStatistic)
    }

    @Test
    fun `delete role statistic`(): Unit = runBlocking {
        val roleStatisticId = UUID.randomUUID()

        userStatisticsUseCase.deleteRoleStatistic(roleStatisticId)

        verify(userRatingRepository).deleteRoleStatistic(roleStatisticId)
    }
}
