package io.critica.infrastructure

import io.critica.domain.UserRole
import io.critica.persistence.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import java.util.*
suspend inline fun ApplicationCall.authorize(
    requiredRoles: List<UserRole>,
    userRepository: UserRepository,
    crossinline block: suspend () -> Unit
) {
    val principal = authentication.principal<UserIdPrincipal>()
    if (principal != null) {
        val user = userRepository.findById(UUID.fromString(principal.name))
        if (user != null && user.role in requiredRoles) {
            block()
        } else {
            respond(HttpStatusCode.Forbidden, "You do not have the required role to access this resource.")
        }
    } else {
        respond(HttpStatusCode.Unauthorized, "You must be authenticated to access this resource.")
    }
}
