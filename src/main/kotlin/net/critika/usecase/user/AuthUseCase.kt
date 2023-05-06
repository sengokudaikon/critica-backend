package net.critika.usecase.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.command.CreateAccount
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepository
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.persistence.exception.UserException
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepository,
    private val userStatisticsUseCase: UserStatisticsUseCase,
    private val userSettingsUseCase: UserSettingsUseCase,
    private val passwordEncoder: Argon2PasswordEncoder,
) {
    suspend fun register(request: CreateAccount): Either<Exception, User> {
        return try {
            val user = userRepository.create(
                request.username,
                request.email,
                request.playerName,
                passwordEncoder.encode(request.password),
            )
            userStatisticsUseCase.createUserRating(user.id.value)
            userSettingsUseCase.createUserSettings(user.id.value)
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun signIn(email: String?, username: String?, password: String): Either<Exception, User> {
        return try {
            if (email == null && username == null) {
                Exception("Email or username must be provided").left()
            }

            val user = if (email != null) {
                userRepository.findByEmail(email)
            } else if (username != null) {
                userRepository.findByUsername(username)
            } else {
                throw UserException.NotFound("Email or username must be provided")
            }

            if (user != null && passwordEncoder.verify(password, user.password)) {
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
