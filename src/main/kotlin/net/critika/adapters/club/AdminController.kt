package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.domain.user.model.UserRole
import net.critika.ports.user.AdminPort

@RouteController
@Tag(name = "Admin")
class AdminController(
    private val adminUseCase: AdminPort,
) : Controller() {

    @ProtectedRoute("firebase")
    @Get("/api/admin/users/requesting-promotion")
    suspend fun listUsersRequestingPromotion(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val users = adminUseCase.findUsersRequestingPromotion()
            call.respond(users)
        }
    }

    @ProtectedRoute("firebase")
    @Post("/api/admin/users/{userId}/promote")
    suspend fun promoteUserToHost(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val userId = fromUid(call)
            adminUseCase.promoteUserToHost(userId)
            call.respond(HttpStatusCode.OK, "User promoted to host")
        }
    }

    @ProtectedRoute("firebase")
    @Post("/api/admin/users/{userId}/reject")
    suspend fun rejectPromotion(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val userId = fromUid(call)
            adminUseCase.rejectPromotion(userId)
            call.respond(HttpStatusCode.OK, "User promotion rejected")
        }
    }
}
