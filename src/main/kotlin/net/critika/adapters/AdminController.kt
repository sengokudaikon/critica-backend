package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.application.user.query.UserQuery
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthPrincipality
import net.critika.infrastructure.authorize
import net.critika.infrastructure.validation.validate
import net.critika.usecase.admin.AdminUseCase
import java.util.*

@RouteController
@Tag(name = "Admin")
class AdminController(
    private val adminUseCase: AdminUseCase,
    private val authPrincipality: AuthPrincipality,
) {

    @ProtectedRoute("jwt")
    @Get("/api/admin/users/requesting-promotion")
    suspend fun listUsersRequestingPromotion(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val users = adminUseCase.findUsersRequestingPromotion()
            call.respond(users)
        }
    }

    @ProtectedRoute("jwt")
    @Post("/api/admin/users/{userId}/promote")
    suspend fun promoteUserToHost(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val userId = call.receive<UserQuery>()
            validate(userId)
            adminUseCase.promoteUserToHost(UUID.fromString(userId.id))
            call.respond(HttpStatusCode.OK, "User promoted to host")
        }
    }

    @ProtectedRoute("jwt")
    @Post("/api/admin/users/{userId}/reject")
    suspend fun rejectPromotion(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val userId = call.receive<UserQuery>()
            validate(userId)
            adminUseCase.rejectPromotion(UUID.fromString(userId.id))
            call.respond(HttpStatusCode.OK, "User promotion rejected")
        }
    }
}
