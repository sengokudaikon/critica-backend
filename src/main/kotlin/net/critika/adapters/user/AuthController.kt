package net.critika.adapters.user

import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.application.user.command.UserCommand
import net.critika.application.user.query.UserQuery
import net.critika.infrastructure.validation.validate
import net.critika.ports.user.AuthPort

@RouteController
@Tag(name = "Auth")
@Tag(name = "User")
class AuthController(
    private val authUseCase: AuthPort,
) : Controller() {
    @ProtectedRoute("firebase")
    @Post("/api/auth/register")
    suspend fun register(call: ApplicationCall) {
        val uid = uid(call)
        val command = call.receive<UserCommand.Create>()
        try {
            validate(command)
        } catch (
            e: Exception,
        ) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Error during registration")
        }

        val userExists = authUseCase.checkIfExists(uid) || authUseCase.checkIfMailExists(command.email)
        if (userExists) {
            call.respond(HttpStatusCode.BadRequest, "User already exists")
        }
        val either = authUseCase.register(uid, command)
        return either.fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Error during registration") },
            {
                call.respond(
                    HttpStatusCode.Created,
                    message = mapOf(
                        "message" to "User created successfully, verify your email to sign in",
                        "uid" to uid,
                    ),
                )
            },
        )
    }

    @ProtectedRoute("firebase")
    @Post("/api/auth/signIn")
    suspend fun signIn(call: ApplicationCall) {
        val uid = uid(call)
        val command = call.receive<UserCommand.SignIn>()
        validate(command)
        val userExists = authUseCase.checkIfExists(uid) ||
            command.email?.let { authUseCase.checkIfMailExists(it) } == true
        if (!userExists) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return
        }

        authUseCase.signIn(uid, command.email).fold(
            { error -> call.respond(HttpStatusCode.BadRequest, error.message ?: "Invalid credentials") },
            { user ->
                call.respond(
                    HttpStatusCode.OK,
                    message = user.toResponse(),
                )
            },
        )
    }

    @Post("/api/auth/userExists")
    suspend fun getUserExists(call: ApplicationCall) {
        val query = call.receive<UserQuery.Exists>()
        validate(query)
        val userExists = query.email?.let { authUseCase.checkIfMailExists(it) } ?: false
        call.respond(HttpStatusCode.OK, message = mapOf("userExists" to userExists))
    }
}
