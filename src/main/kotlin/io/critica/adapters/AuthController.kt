package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.user.command.CreateAccount
import io.critica.application.user.command.RefreshToken
import io.critica.application.user.command.SignIn
import io.critica.application.user.command.SignOut
import io.critica.infrastructure.Security
import io.critica.infrastructure.validation.Validator
import io.critica.usecase.user.AuthUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

@RouteController
class AuthController(
    private val authUseCase: AuthUseCase,
    private val security: Security,
) {
    private val validator: jakarta.validation.Validator = Validator().init()

    @Post("/api/auth/register")
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<CreateAccount>()
        validator.validate(request)
        authUseCase.register(request).fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Error during registration") },
            { user ->
                val accessToken = security.generateAccessToken(user.id.value)
                val refreshToken = security.generateRefreshToken(user.id.value)

                call.respond(
                    HttpStatusCode.Created,
                    message = mapOf(
                        "accessToken" to accessToken,
                        "refreshToken" to refreshToken
                    )
                )
            }
        )
    }

    @Post("/api/auth/signIn")
    suspend fun signIn(call: ApplicationCall) {
        val request = call.receive<SignIn>()
        validator.validate(request)
        authUseCase.signIn(request).fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Invalid credentials") },
            { user ->
                val accessToken = security.generateAccessToken(user.id.value)
                val refreshToken = security.generateRefreshToken(user.id.value)

                call.respond(
                    HttpStatusCode.OK,
                    message = mapOf(
                        "accessToken" to accessToken,
                        "refreshToken" to refreshToken
                    )
                )
            }
        )
    }

    @Post("/api/auth/refresh")
    suspend fun refresh(call: ApplicationCall) {
        val request = call.receive<RefreshToken>()
        validator.validate(request)
        security.verifyRefreshToken(UUID.fromString(request.id), request.refreshToken)

        val accessToken = security.generateAccessToken(UUID.fromString(request.id))

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "accessToken" to accessToken
            )
        )
    }

    @Post("/api/auth/signout")
    suspend fun signOut(call: ApplicationCall) {
        val request = call.receive<SignOut>()
        validator.validate(request)
        security.verifyRefreshToken(UUID.fromString(request.id), request.refreshToken)

        security.invalidateRefreshToken(UUID.fromString(request.id))

        call.respond(HttpStatusCode.NoContent)
    }
}
