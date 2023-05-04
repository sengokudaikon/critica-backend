package net.critika.usecase.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.command.CreateAccount
import net.critika.application.user.command.SignIn
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepository
import net.critika.persistence.repository.UserRepositoryImpl
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepository,
    private val userStatisticsUseCase: UserStatisticsUseCase,
    private val userSettingsUseCase: UserSettingsUseCase,
    private val passwordEncoder: Argon2PasswordEncoder
) {
    suspend fun register(request: CreateAccount): Either<Exception, User> {
        return try {
            val user = userRepository.create(
                request.username,
                request.email,
                passwordEncoder.encode(request.password)
            )
            userStatisticsUseCase.createUserRating(user.id.value)
            userSettingsUseCase.createUserSettings(user.id.value)
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun signIn(request: SignIn): Either<Exception, User> {
        return try {
            if (request.email == null && request.username == null) {
                Exception("Email or username must be provided").left()
            }

            val user = if (request.email != null) {
                userRepository.findByEmail(request.email)
            } else {
                userRepository.findByUsername(request.username!!)
            }

            if (user != null && passwordEncoder.verify(request.password, user.password)) {
                user.right()
            } else {
                Exception("Invalid username or password").left()
            }
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun checkIfExists(username: String?, email: String?): Boolean {
        val userByMail = email?.let { userRepository.findByEmail(it) }
        val usernameExists = username?.let { userRepository.findByUsername(it) }

        return userByMail != null || usernameExists != null
    }
}
