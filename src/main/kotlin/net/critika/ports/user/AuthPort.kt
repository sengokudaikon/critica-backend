package net.critika.ports.user

import arrow.core.Either
import net.critika.application.user.command.UserCommand
import net.critika.domain.user.model.User

interface AuthPort {
    suspend fun register(uid: String, command: UserCommand.Create): Either<Exception, User>
    suspend fun signIn(uid: String, email: String?, username: String?, password: String): Either<Exception, User>
    suspend fun checkIfMailExists(email: String): Boolean

    suspend fun checkIfUsernameExists(username: String): Boolean
    suspend fun checkIfExists(uid: String): Boolean
    suspend fun getUserByUid(uid: String): User?
    suspend fun createFirebaseUser(email: String, username: String, password: String): String
    suspend fun signInProvider(uid: String, email: String, username: String, deviceToken: String): Either<Exception, User>
}
