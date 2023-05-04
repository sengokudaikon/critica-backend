package net.critika.infrastructure

import net.critika.domain.user.model.UserRole
import net.critika.persistence.repository.UserRepositoryImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*
suspend inline fun ApplicationCall.authorize(
    requiredRoles: List<UserRole>,
    userRepository: UserRepositoryImpl,
    crossinline block: suspend () -> Unit
) {
    val userId = getUserId()
    if (userId != null) {
        val user = userRepository.findById(userId)
        if (user != null && user.role in requiredRoles) {
            block()
        } else {
            respond(HttpStatusCode.Forbidden, "You do not have the required role to access this resource.")
        }
    } else {
        respond(HttpStatusCode.Unauthorized, "You must be authenticated to access this resource.")
    }
}

suspend inline fun ApplicationCall.getUserId(): UUID? {
    val userIdPrincipal = principal<JWTPrincipal>()

    if (userIdPrincipal == null) {
        respond(HttpStatusCode.Unauthorized, "No user id found")
        return null
    }

    val claim = userIdPrincipal.payload.getClaim("userId")
    return UUID.fromString(claim.asString())
}

