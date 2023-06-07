package net.critika.infrastructure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.authentication.getUserId

class AuthorizationService(private val userRepository: UserRepositoryPort) {

    suspend fun <T> authorize(
        call: ApplicationCall,
        requiredRoles: List<UserRole>,
        block: suspend () -> T,
    ) {
        val userId = call.getUserId()
        if (userId != null) {
            val user = userRepository.findById(userId)
            if (user != null && user.role in requiredRoles) {
                block()
            } else {
                call.respond(HttpStatusCode.Forbidden, "You do not have the required role to access this resource.")
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "You must be authenticated to access this resource.")
        }
    }
}
