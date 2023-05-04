package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.application.user.command.CreateAccount
import net.critika.application.user.command.RefreshToken
import net.critika.application.user.command.SignIn
import net.critika.application.user.command.SignOut
import net.critika.infrastructure.Security
import net.critika.infrastructure.getUserId
import net.critika.infrastructure.validation.validate
import net.critika.usecase.user.AuthUseCase
import net.critika.usecase.user.UserSettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@RouteController
@Tag(name = "Auth")
@Tag(name = "User")
class AuthController(
    private val authUseCase: AuthUseCase,
) : KoinComponent {
    private val settingsUseCase: UserSettingsUseCase by inject()
    private val security: Security by inject()

    @Post("/api/auth/register")
    suspend fun register(call: ApplicationCall) {
        val request = call.receive<CreateAccount>()
        try {
            validate(request)
        } catch (
            e: Exception,
        ) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Error during registration")
        }
        val userExists = authUseCase.checkIfExists(request.username, request.email)
        if (userExists) {
            call.respond(HttpStatusCode.BadRequest, "User already exists")
        }
        val either = authUseCase.register(request)
        return either.fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Error during registration") },
            { user ->
                settingsUseCase.requestEmailVerification(user.id.value)
                call.respond(
                    HttpStatusCode.Created,
                    message = "User created successfully, verify your email to sign in",
                )
            },
        )
    }

    @Post("/api/auth/signIn")
    suspend fun signIn(call: ApplicationCall) {
        val request = call.receive<SignIn>()
        validate(request)
        val userExists = authUseCase.checkIfExists(request.username, request.email)
        if (!userExists) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return
        }

        authUseCase.signIn(request).fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Invalid credentials") },
            { user ->
                val accessToken = security.generateAccessToken(user.id.value)
                val refreshToken = security.generateRefreshToken(user.id.value)

                call.respond(
                    HttpStatusCode.OK,
                    message = mapOf(
                        "accessToken" to accessToken,
                        "refreshToken" to refreshToken,
                    ),
                )
            },
        )
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/auth/refresh")
    suspend fun refresh(call: ApplicationCall) {
        val request = call.receive<RefreshToken>()
        val user = call.getUserId()

        if (user != null) {
            if (security.verifyRefreshToken(user, request.refreshToken)) {
                val accessToken = security.generateAccessToken(user)
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "accessToken" to accessToken,
                    ),
                )
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to refresh token")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
        }
    }

    @Post("/api/auth/signout")
    suspend fun signOut(call: ApplicationCall) {
        val request = call.receive<SignOut>()
        security.verifyRefreshToken(UUID.fromString(request.id), request.refreshToken)

        security.invalidateRefreshToken(UUID.fromString(request.id))

        call.respond(HttpStatusCode.NoContent)
    }
}
