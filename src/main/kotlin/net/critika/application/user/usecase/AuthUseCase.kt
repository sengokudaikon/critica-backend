package net.critika.application.user.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.command.UserCommand
import net.critika.application.user.command.UserRatingCommand
import net.critika.application.user.command.UserSettingsCommand
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.exception.UserException
import net.critika.ports.user.AuthPort
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepositoryPort,
    private val userRatingUseCase: UserRatingUseCase,
    private val userSettingsUseCase: UserSettingsUseCase,
) : AuthPort {
    override suspend fun register(uid: String, command: UserCommand.Create): Either<Exception, User> {
        return try {
            val user = userRepository.create(
                uid,
                command.email,
                command.playerName,
            )
            userRatingUseCase.create(UserRatingCommand.Create(user.id.value))
            userSettingsUseCase.create(UserSettingsCommand.Create(user.id.value))
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun signIn(uid: String, email: String?): Either<Exception, User> {
        return try {
            val user = userRepository.findByUid(uid)

            require(user != null) { UserException.NotFound("User not found") }
            if (email != null) {
                require(user.email == email) { UserException.Invalid("Invalid email") }
            }

            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun checkIfMailExists(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    override suspend fun checkIfExists(uid: String): Boolean {
        return this.getUserByUid(uid) != null
    }

    override suspend fun getUserByUid(uid: String): User? {
        return userRepository.findByUid(uid)
    }
}
