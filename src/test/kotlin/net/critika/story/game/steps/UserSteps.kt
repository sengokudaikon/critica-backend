package net.critika.story.game.steps // Steps.kt

import arrow.core.Either
import io.qameta.allure.Step
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import net.critika.application.user.command.UserCommand
import net.critika.application.user.usecase.AuthUseCase
import net.critika.domain.user.model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

open class UserSteps : KoinComponent {
    private val userUseCase: AuthUseCase by inject()

    @Step("Register user {username}")
    open suspend fun register(
        email: String,
        username: String,
        playerName: String,
        password: String,
    ): Either<Exception, User> {
        return userUseCase.register(UUID.generateUUID(Random).toString(), UserCommand.Create(email, playerName))
    }

    @Step("Login user {username}")
    open suspend fun login(email: String?, username: String?, password: String): Either<Exception, User> {
        return userUseCase.signIn(UUID.generateUUID(Random).toString(), email)
    }
}
