package io.critica.usecase.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.critica.application.user.command.CreateAccount
import io.critica.application.user.command.SignIn
import io.critica.infrastructure.Argon2PasswordEncoder
import io.critica.domain.User
import io.critica.persistence.repository.UserRepository
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: Argon2PasswordEncoder
) {
    suspend fun register(request: CreateAccount): Either<Exception, User> {
        return try {
            userRepository.create(
                request.username,
                request.email,
                passwordEncoder.encode(request.password)
            ).right()
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

            if (passwordEncoder.verify(request.password, user.password)) {
                user.right()
            } else {
                Exception("Invalid username or password").left()
            }
        } catch (e: Exception) {
            e.left()
        }
    }
}
