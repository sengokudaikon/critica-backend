package net.critika.infrastructure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import net.critika.persistence.exception.GameException
import net.critika.persistence.exception.LobbyException

fun Application.handleErrors() {
    install(StatusPages) {
        exception<InternalError> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<Throwable> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respond(HttpStatusCode.InternalServerError, "500: An unexpected error occurred")
        }
        exception<IllegalArgumentException> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
        exception<BadRequestException> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respond(HttpStatusCode.BadRequest, "400: ${cause.localizedMessage}")
        }
        exception<NotFoundException> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respondText(text = "404: $cause", status = HttpStatusCode.NotFound)
        }
        exception<GameException> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
        exception<LobbyException> { call, cause ->
            this@handleErrors.log.error(cause.localizedMessage)
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
    }
}
