package net.critika.unit.usecase.user

import arrow.core.Either
import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.user.command.UserCommand
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.response.UserSettingsResponse
import net.critika.application.user.usecase.AuthUseCase
import net.critika.application.user.usecase.UserSettingsUseCase
import net.critika.application.user.usecase.UserStatisticsUseCase
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.model.UserSetting
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.persistence.user.repository.UserSettingsRepository
import net.critika.unit.Helpers.getMockUser
import org.jetbrains.exposed.dao.id.EntityID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

class AuthUseCaseTest {
    private val userRepository: UserRepositoryPort = mock()
    private val userStatisticsUseCase: UserStatisticsUseCase = mock()
    private val userSettingsUseCase: UserSettingsUseCase = mock()
    val uid = UUID.generateUUID(Random).toString()
    private val authUseCase = AuthUseCase(
        userRepository,
        userStatisticsUseCase,
        userSettingsUseCase,
    )

    @BeforeEach
    fun setUp() {
        clearInvocations(userRepository, userStatisticsUseCase, userSettingsUseCase)
    }

    @Test
    fun `register success`() = runBlocking {
        // Given
        val request = UserCommand.Create("email@example.com", "playerName")
        val createdUser = getMockUser()
        val mockId: EntityID<UUID> = mock()
        val userRating: UserRating = mock()
        val userSettingsRepository: UserSettingsRepository = mock()
        val userSetting: UserSetting = mock()
        val userSettings: UserSettingsResponse = mock()
        whenever(userRepository.create(any(), any(), any())).thenReturn(createdUser)
        whenever(userStatisticsUseCase.createUserRating(any())).thenReturn(userRating)
        whenever(userSettingsRepository.createUserSettings(any())).thenReturn(userSetting)
        whenever(userSettingsUseCase.create(UserSettingsCommand.Create(any(), any()))).thenReturn(
            Either.Right(userSettings),
        )
        whenever(createdUser.id).thenReturn(mockId)
        // When
        val result = authUseCase.register(uid, request)

        // Then
        verify(userRepository, times(1)).create(uid, request.email, request.playerName)
        verify(userStatisticsUseCase, times(1)).createUserRating(createdUser.id.value)
        verify(userSettingsUseCase, times(1)).create(UserSettingsCommand.Create(createdUser.id.value))
        Assertions.assertEquals(Either.Right(createdUser), result)
    }

    @Test
    fun `signIn success by email`() = runBlocking {
        // Given
        val request = UserCommand.SignIn("email@example.com")
        val user = getMockUser()
        whenever(request.email?.let { userRepository.findByEmail(it) }).thenReturn(user)

        // When
        val result = authUseCase.signIn(uid, request.email)

        // Then
        request.email?.let { verify(userRepository, times(1)).findByEmail(it) }
        Assertions.assertEquals(Either.Right(user), result)
    }

    @Test
    fun `signIn failure no email or username`() = runBlocking {
        // Given
        val request = UserCommand.SignIn(null)

        // When
        val result = authUseCase.signIn(uid, request.email)

        // Then
        verify(userRepository, never()).findByEmail(any())

        val error = (result as Either.Left).value
        Assertions.assertEquals(null, error.message)
    }

    @Test
    fun `checkIfExists true`() = runBlocking {
        // Given
        val user = getMockUser()
        whenever(userRepository.findByEmail(user.email)).thenReturn(user)

        // When
        val exists = authUseCase.checkIfExists(uid)

        // Then
        verify(userRepository, times(1)).findByEmail(user.email)
        Assertions.assertEquals(true, exists)
    }

    @Test
    fun `checkIfExists false`() = runBlocking {
        // Given
        whenever(userRepository.findByEmail(any())).thenReturn(null)

        // When
        val exists = authUseCase.checkIfExists(UUID.generateUUID(Random).toString())

        // Then
        verify(userRepository, times(1)).findByEmail("nonexistent_email@example.com")
        Assertions.assertEquals(false, exists)
    }
}
