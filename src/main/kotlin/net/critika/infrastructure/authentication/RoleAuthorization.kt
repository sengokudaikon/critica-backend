package net.critika.infrastructure.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.uuid.UUID

suspend inline fun ApplicationCall.getUserId(): UUID? {
    val userIdPrincipal = principal<JWTPrincipal>()

    if (userIdPrincipal == null) {
        respond(HttpStatusCode.Unauthorized, "No user id found")
        return null
    }

    val claim = userIdPrincipal.payload.getClaim("userId")
    return UUID(claim.asString())
}
