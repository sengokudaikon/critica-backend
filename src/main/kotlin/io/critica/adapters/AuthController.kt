package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.user.request.CreateAccount
import io.critica.application.user.request.RefreshToken
import io.critica.application.user.request.SignIn
import io.critica.application.user.request.SignOut
import io.critica.infrastructure.Security
import io.critica.usecase.user.AuthUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

@RouteController
class AuthController(
    private val authUseCase: AuthUseCase,
    private val security: Security
) {
    @Post("api/auth/register")
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<CreateAccount>()
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

    @Post("api/auth/signIn")
    suspend fun signIn(call: ApplicationCall) {
        val request = call.receive<SignIn>()
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

    @Post("api/auth/refresh")
    suspend fun refresh(call: ApplicationCall) {
        val request = call.receive<RefreshToken>()
        security.verifyRefreshToken(UUID.fromString(request.id), request.refreshToken)

        val accessToken = security.generateAccessToken(UUID.fromString(request.id))

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "accessToken" to accessToken
            )
        )
    }

    @Post("api/auth/signout")
    suspend fun signOut(call: ApplicationCall) {
        val request = call.receive<SignOut>()
        security.verifyRefreshToken(UUID.fromString(request.id), request.refreshToken)

        security.invalidateRefreshToken(UUID.fromString(request.id))

        call.respond(HttpStatusCode.NoContent)
    }
}
