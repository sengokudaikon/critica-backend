package net.critika.unit.usecase.user

import kotlinx.coroutines.runBlocking
import net.critika.domain.Language
import net.critika.domain.user.model.UserSetting
import net.critika.domain.user.repository.UserRepository
import net.critika.domain.user.repository.UserSettingsRepository
import net.critika.domain.user.repository.UserVerificationCodeRepository
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.persistence.db.UserSettings
import net.critika.persistence.db.Users
import net.critika.unit.Helpers.getMockUser
import net.critika.usecase.user.UserSettingsUseCase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testng.annotations.BeforeTest

@Testcontainers
class UserSettingsUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: Argon2PasswordEncoder
    private lateinit var verificationCodeRepository: UserVerificationCodeRepository
    private lateinit var userSettingsRepository: UserSettingsRepository

    private lateinit var userSettingsUseCase: UserSettingsUseCase

    companion object {
        @Container
        val postgreSQLContainer = PostgreSQLContainer<Nothing>(
            DockerImageName.parse("postgres:13.3")
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
            password = postgreSQLContainer.password
        )
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, UserSettings)
        }
    }

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        passwordEncoder = mock()
        verificationCodeRepository = mock()
        userSettingsRepository = mock()

        userSettingsUseCase = UserSettingsUseCase(userRepository, passwordEncoder, verificationCodeRepository, userSettingsRepository)
    }

    @Test
    fun `createUserSettings successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val language = "English"
        val expectedUserSetting: UserSetting = mock<UserSetting>().apply{
            this.language = Language.ENGLISH
        }

        whenever(userSettingsRepository.createUserSettings(userId, language)).thenReturn(expectedUserSetting)

        // When
        val result = userSettingsUseCase.createUserSettings(userId, language)

        // Then
        verify(userSettingsRepository).createUserSettings(userId, language)
        assertEquals(expectedUserSetting, result)
    }

    @Test
    fun `changeUsername successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val newUsername = "newUsername"

        // When
        userSettingsUseCase.changeUsername(userId, newUsername)

        // Then
        verify(userRepository).updateUsername(userId, newUsername)
    }

    @Test
    fun `changePassword successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val oldPassword = "oldPassword"
        val newPassword = "newPassword"
        val newEncodedPassword = "new_encoded_password"
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)
        whenever(passwordEncoder.verify(newPassword, user.password)).thenReturn(false)
        whenever(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword)

        // When
        userSettingsUseCase.changePassword(userId, newPassword)

        // Then
        verify(userRepository).findById(userId)
        verify(passwordEncoder).verify(newPassword, user.password)
        verify(passwordEncoder).encode(newPassword)
        verify(userRepository).updatePassword(userId, newEncodedPassword)
    }

    @Test
    fun `changeLanguage successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val newLanguage = "es"
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.changeLanguage(userId, newLanguage)

        // Then
        verify(userRepository).findById(userId)
        verify(userSettingsRepository).updateLanguage(userId, newLanguage)
    }

    @Test
    fun `changePushNotifications successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val pushNotifications = true
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.changePushNotifications(userId, pushNotifications)

        // Then
        verify(userRepository).findById(userId)
        verify(userSettingsRepository).updatePushNotifications(userId, pushNotifications)
    }

    @Test
    fun `changePublicVisibility successfully`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val publicVisibility = true
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.changePublicVisibility(userId, publicVisibility)

        // Then
        verify(userRepository).findById(userId)
        verify(userSettingsRepository).updatePublicVisibility(userId, publicVisibility)
    }

    @Test
    fun `getUserSettings successfully`() = runBlocking {
        // Given
        val user = getMockUser()
        val userSetting = mock<UserSetting> {
            on { userId } doReturn user
            on { emailVerified } doReturn true
            on { publicVisibility } doReturn true
            on { pushNotifications } doReturn true
            on { language } doReturn Language.ENGLISH
            on { promotion } doReturn true
        }

        val userIdMock = UUID.randomUUID()

        whenever(userRepository.findById(userIdMock)).thenReturn(user)
        whenever(userSettingsRepository.getUserSettingsByUserId(userIdMock)).thenReturn(userSetting)

        // When
        val result = userSettingsUseCase.getUserSettings(userIdMock)

        // Then
        verify(userRepository).findById(userIdMock)
        verify(userSettingsRepository).getUserSettingsByUserId(userIdMock)
        result.fold(
            { fail("Expecting a right value, got left: $it") },
            {
                assertEquals("username", it.username)
                assertEquals(true, it.emailConfirmed)
                assertEquals(true, it.publicVisibility)
                assertEquals(true, it.pushNotificationsEnabled)
                assertEquals("ENGLISH", it.language)
                assertEquals(true, it.promoted)
            }
        )
    }
}
