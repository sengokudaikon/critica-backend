package net.critika.usecase.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.critika.application.user.command.CreateAccount
import net.critika.domain.user.model.User
import net.critika.domain.user.repository.UserRepository
import net.critika.infrastructure.Argon2PasswordEncoder
import net.critika.infrastructure.authentication.FirebaseAdmin
import net.critika.persistence.exception.UserException
import org.koin.core.annotation.Single

@Single
class AuthUseCase(
    private val userRepository: UserRepository,
    private val userStatisticsUseCase: UserStatisticsUseCase,
    private val userSettingsUseCase: UserSettingsUseCase,
    private val passwordEncoder: Argon2PasswordEncoder,
) {
    suspend fun register(uid: String, request: CreateAccount): Either<Exception, User> {
        return try {
            val user = userRepository.create(
                uid,
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

    suspend fun signIn(uid: String, email: String?, username: String?, password: String): Either<Exception, User> {
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

    suspend fun checkIfExists(uid: String, username: String?, email: String?): Boolean {
        val userByUid = uid.let { userRepository.findByUid(it) }
        val userByMail = email?.let { userRepository.findByEmail(it) }
        val usernameExists = username?.let { userRepository.findByUsername(it) }

        return userByMail != null || usernameExists != null || userByUid != null
    }

    suspend fun getUserByUid(uid: String): User? {
        return userRepository.findByUid(uid)
    }

    suspend fun createFirebaseUser(email: String, username: String, password: String): String {
        val userRecord = FirebaseAdmin.createUserWithEmailAndPassword(email, username, password)
        return userRecord.uid
    }

    suspend fun signInProvider(uid: String, email: String, username: String, deviceToken: String): Either<Exception, User> {
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
