package net.critika.infrastructure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import kotlinx.uuid.UUID
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.authentication.FirebasePrincipal
import net.critika.infrastructure.exception.UserException

class AuthorizationService(private val userRepository: UserRepositoryPort) {

    suspend fun <T> authorize(
        call: ApplicationCall,
        requiredRoles: List<UserRole>,
        block: suspend () -> T,
    ) {
        val user = userRepository.findById(getUserId(call)) ?: throw UserException.NotFound()
        if (user.role in requiredRoles) {
            block()
        } else {
            call.respond(HttpStatusCode.Forbidden, "You do not have the required role to access this resource.")
        }
    }

    suspend fun getUserId(call: ApplicationCall): UUID {
        val uid = call.principal<FirebasePrincipal>()?.uid ?: throw UserException.Unauthorized("Unauthorized Firebase user")
        val user = userRepository.findByUid(uid) ?: throw UserException.NotFound()
        return user.id.value
    }
}
