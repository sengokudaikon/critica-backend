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
import net.critika.infrastructure.Argon2PasswordEncoder
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
    private val passwordEncoder: Argon2PasswordEncoder = mock()
    val uid = UUID.generateUUID(Random).toString()
    private val authUseCase = AuthUseCase(
        userRepository,
        userStatisticsUseCase,
        userSettingsUseCase,
        passwordEncoder,
    )

    @BeforeEach
    fun setUp() {
        clearInvocations(userRepository, userStatisticsUseCase, userSettingsUseCase, passwordEncoder)
    }

    @Test
    fun `register success`() = runBlocking {
        // Given
        val request = UserCommand.Create("email@example.com", "password", "username", "playerName")
        val encodedPassword = "encoded_password"
        val createdUser = getMockUser()
        val mockId: EntityID<UUID> = mock()
        val userRating: UserRating = mock()
        val userSettingsRepository: UserSettingsRepository = mock()
        val userSetting: UserSetting = mock()
        val userSettings: UserSettingsResponse = mock()
        whenever(userRepository.create(any(), any(), any(), any(), any())).thenReturn(createdUser)
        whenever(passwordEncoder.encode(any())).thenReturn(encodedPassword)
        whenever(userStatisticsUseCase.createUserRating(any())).thenReturn(userRating)
        whenever(userSettingsRepository.createUserSettings(any())).thenReturn(userSetting)
        whenever(userSettingsUseCase.create(UserSettingsCommand.Create(any(), any()))).thenReturn(
            Either.Right(userSettings))
        whenever(createdUser.id).thenReturn(mockId)
        // When
        val result = authUseCase.register(uid, request)

        // Then
        verify(userRepository, times(1)).create(uid, request.username, request.email, request.playerName, encodedPassword)
        verify(passwordEncoder, times(1)).encode(request.password)
        verify(userStatisticsUseCase, times(1)).createUserRating(createdUser.id.value)
        verify(userSettingsUseCase, times(1)).create(UserSettingsCommand.Create(createdUser.id.value))
        Assertions.assertEquals(Either.Right(createdUser), result)
    }

    @Test
    fun `signIn success by email`() = runBlocking {
        // Given
        val request = UserCommand.SignIn("email@example.com", null, "password")
        val user = getMockUser()
        whenever(request.email?.let { userRepository.findByEmail(it) }).thenReturn(user)
        whenever(passwordEncoder.verify(request.password, user.password)).thenReturn(true)

        // When
        val result = authUseCase.signIn(uid, request.email, request.username, request.password)

        // Then
        request.email?.let { verify(userRepository, times(1)).findByEmail(it) }
        verify(passwordEncoder, times(1)).verify(request.password, user.password)
        Assertions.assertEquals(Either.Right(user), result)
    }

    @Test
    fun `signIn success by username`() = runBlocking {
        // Given
        val request = UserCommand.SignIn(null, "username", "password")
        val user = getMockUser()
        whenever(request.username?.let { userRepository.findByUsername(it) }).thenReturn(user)
        whenever(passwordEncoder.verify(request.password, user.password)).thenReturn(true)

        // When
        val result = authUseCase.signIn(uid, request.email, request.username, request.password)

        // Then
        request.username?.let { verify(userRepository, times(1)).findByUsername(it) }
        verify(passwordEncoder, times(1)).verify(request.password, user.password)
        Assertions.assertEquals(Either.Right(user), result)
    }

    @Test
    fun `signIn failure no email or username`() = runBlocking {
        // Given
        val request = UserCommand.SignIn(null, null, "password")

        // When
        val result = authUseCase.signIn(uid, request.email, request.username, request.password)

        // Then
        verify(userRepository, never()).findByEmail(any())
        verify(userRepository, never()).findByUsername(any())
        verify(passwordEncoder, never()).verify(any(), any())

        val error = (result as Either.Left).value
        Assertions.assertEquals(null, error.message)
    }

    @Test
    fun `signIn failure wrong password`() = runBlocking {
        // Given
        val request = UserCommand.SignIn("email@example.com", null, "password")
        val user = getMockUser()
        whenever(request.email?.let { userRepository.findByEmail(it) }).thenReturn(user)
        whenever(passwordEncoder.verify(request.password, user.password)).thenReturn(false)

        // When
        val result = authUseCase.signIn(uid, request.email, request.username, request.password)

        // Then
        request.email?.let { verify(userRepository, times(1)).findByEmail(it) }
        verify(passwordEncoder, times(1)).verify(request.password, user.password)

        val error = (result as Either.Left).value
        Assertions.assertEquals("Invalid username or password", error.message)
    }

    @Test
    fun `checkIfExists true`() = runBlocking {
        // Given
        val user = getMockUser()
        whenever(userRepository.findByEmail(user.email)).thenReturn(user)
        whenever(userRepository.findByUsername(user.username)).thenReturn(user)

        // When
        val exists = authUseCase.checkIfExists(uid)

        // Then
        verify(userRepository, times(1)).findByEmail(user.email)
        verify(userRepository, times(1)).findByUsername(user.username)
        Assertions.assertEquals(true, exists)
    }

    @Test
    fun `checkIfExists false`() = runBlocking {
        // Given
        whenever(userRepository.findByEmail(any())).thenReturn(null)
        whenever(userRepository.findByUsername(any())).thenReturn(null)

        // When
        val exists = authUseCase.checkIfExists(UUID.generateUUID(Random).toString())

        // Then
        verify(userRepository, times(1)).findByEmail("nonexistent_email@example.com")
        verify(userRepository, times(1)).findByUsername("nonexistent_username")
        Assertions.assertEquals(false, exists)
    }
}
