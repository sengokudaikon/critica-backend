package net.critika.unit.usecase.user

import kotlinx.coroutines.runBlocking
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.usecase.UserSettingsUseCase
import net.critika.domain.user.model.Language
import net.critika.domain.user.model.UserSetting
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.domain.user.repository.UserSettingsRepositoryPort
import net.critika.helpers.Helpers.getMockUser
import net.critika.persistence.user.entity.UserSettings
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testng.annotations.BeforeTest
import kotlin.random.Random

@Testcontainers
class UserSettingsUseCaseTest {

    private lateinit var userRepository: UserRepositoryPort
    private lateinit var userSettingsRepository: UserSettingsRepositoryPort

    private lateinit var userSettingsUseCase: UserSettingsUseCase

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
        userRepository = mock()
        userSettingsRepository = mock()

        userSettingsUseCase = UserSettingsUseCase(
            userRepository,
            userSettingsRepository,
        )
    }

    @Test
    fun `createUserSettings successfully`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val language = "English"
        val expectedUserSetting: UserSetting = mock<UserSetting>().apply {
            this.language = Language.ENGLISH
        }

        whenever(userSettingsRepository.createUserSettings(userId, language)).thenReturn(expectedUserSetting)

        // When
        val result = userSettingsUseCase.create(UserSettingsCommand.Create(userId, language)).getOrNull()

        // Then
        verify(userSettingsRepository).createUserSettings(userId, language)
        assertEquals(expectedUserSetting.toResponse(), result)
    }

    @Test
    fun `changeLanguage successfully`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val newLanguage = "es"
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.update(UserSettingsCommand.Update.Language(userId, newLanguage))

        // Then
        verify(userRepository).findById(userId)
        verify(userSettingsRepository).updateLanguage(userId, newLanguage)
    }

    @Test
    fun `changePushNotifications successfully`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val pushNotifications = true
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.update(UserSettingsCommand.Update.PushNotification(userId, pushNotifications))

        // Then
        verify(userRepository).findById(userId)
        verify(userSettingsRepository).updatePushNotifications(userId, pushNotifications)
    }

    @Test
    fun `changePublicVisibility successfully`() = runBlocking {
        // Given
        val userId = UUID.generateUUID(Random)
        val publicVisibility = true
        val user = getMockUser()

        whenever(userRepository.findById(userId)).thenReturn(user)

        // When
        userSettingsUseCase.update(UserSettingsCommand.Update.PublicVisibility(userId, publicVisibility))

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

        val userIdMock = UUID.generateUUID(Random)

        whenever(userRepository.findById(userIdMock)).thenReturn(user)
        whenever(userSettingsRepository.getUserSettingsByUserId(userIdMock)).thenReturn(userSetting)

        // When
        val result = userSettingsUseCase.get(userIdMock)

        // Then
        verify(userRepository).findById(userIdMock)
        verify(userSettingsRepository).getUserSettingsByUserId(userIdMock)
        result.fold(
            { fail("Expecting a right value, got left: $it") },
            {
                assertEquals(true, it.emailConfirmed)
                assertEquals(true, it.publicVisibility)
                assertEquals(true, it.pushNotificationsEnabled)
                assertEquals("ENGLISH", it.language)
                assertEquals(true, it.promoted)
            },
        )
    }
}
