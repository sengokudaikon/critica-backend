package net.critika.application.user.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.command.UserCommand
import net.critika.application.user.command.UserSettingsCommand
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.infrastructure.authentication.FirebaseAdmin
import net.critika.infrastructure.exception.UserException
import net.critika.ports.user.AuthPort
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepositoryPort,
    private val userStatisticsUseCase: UserStatisticsUseCase,
    private val userSettingsUseCase: UserSettingsUseCase,
    private val passwordEncoder: Argon2PasswordEncoder,
): AuthPort {
    override suspend fun register(uid: String, command: UserCommand.Create): Either<Exception, User> {
        return try {
            val user = userRepository.create(
                uid,
                command.username,
                command.email,
                command.playerName,
                passwordEncoder.encode(command.password),
            )
            userStatisticsUseCase.createUserRating(user.id.value)
            userSettingsUseCase.create(UserSettingsCommand.Create(user.id.value))
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun signIn(uid: String, email: String?, username: String?, password: String): Either<Exception, User> {
        return try {
            val user = userRepository.findByUid(uid)

            require(user != null) { UserException.NotFound("User not found") }
            if (email != null) {
                require(user.email == email) { UserException.Invalid("Invalid email") }
            }

            if (username != null) {
                require(user.username == username) { UserException.Invalid("Invalid username") }
            }

            require(passwordEncoder.verify(password, user.password)) { UserException.Invalid("Invalid password") }
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun checkIfMailExists(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    override suspend fun checkIfUsernameExists(username: String): Boolean {
        return userRepository.findByUsername(username) != null
    }

    override suspend fun checkIfExists(uid: String): Boolean {
        return this.getUserByUid(uid) != null
    }

    override suspend fun getUserByUid(uid: String): User? {
        return userRepository.findByUid(uid)
    }

    override suspend fun createFirebaseUser(email: String, username: String, password: String): String {
        val userRecord = FirebaseAdmin.createUserWithEmailAndPassword(email, username, password)
        return userRecord.uid
    }

    override suspend fun signInProvider(uid: String, email: String, username: String, deviceToken: String): Either<Exception, User> {
        return try {
            val user = userRepository.findByUid(uid)

            require(user != null) { UserException.NotFound("User not found") }
            require(user.email == email) { UserException.Invalid("Invalid email") }
            require(user.username == username) { UserException.Invalid("Invalid username") }
            userRepository.addDeviceToken(user.id.value, deviceToken)
            user.right()
        } catch (e: Exception) {
            e.left()
        }
    }
}
