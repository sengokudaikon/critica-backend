package net.critika.story.game.steps // Steps.kt

import arrow.core.Either
import io.qameta.allure.Step
import net.critika.application.user.command.CreateAccount
import net.critika.domain.user.model.User
import net.critika.usecase.user.AuthUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

open class UserSteps : KoinComponent {
    private val userUseCase: AuthUseCase by inject()

    @Step("Register user {username}")
    open suspend fun register(
        email: String,
        username: String,
        playerName: String,
        password: String,
    ): Either<Exception, User> {
        return userUseCase.register(UUID.randomUUID().toString(), CreateAccount(email, password, username, playerName))
    }

    @Step("Login user {username}")
    open suspend fun login(email: String?, username: String?, password: String): Either<Exception, User> {
        return userUseCase.signIn(UUID.randomUUID().toString(), email, username, password)
    }
}
