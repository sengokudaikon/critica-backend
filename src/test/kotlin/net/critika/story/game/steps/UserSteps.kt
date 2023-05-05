package net.critika.story.game.steps // Steps.kt

import arrow.core.Either
import io.qameta.allure.Step
import net.critika.application.user.command.CreateAccount
import net.critika.application.user.command.SignIn
import net.critika.domain.user.model.User
import net.critika.usecase.user.AuthUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class UserSteps : KoinComponent {
    private val userUseCase: AuthUseCase by inject()

    @Step("Register user {username}")
    open suspend fun register(email: String, username: String, password: String): Either<Exception, User> {
        return userUseCase.register(CreateAccount(email, username, password))
    }

    @Step("Login user {username}")
    open suspend fun login(email: String?, username: String?, password: String): Either<Exception, User> {
        return userUseCase.signIn(SignIn(email, username, password))
    }
}