package net.critika.adapters.user

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.application.user.command.UserSettingsCommand
import net.critika.ports.user.UserRatingPort
import net.critika.ports.user.UserSettingsPort

@RouteController
@Tag(name = "User")
class UserController(
    private val userSettingsUseCase: UserSettingsPort,
    private val userStatisticsUseCase: UserRatingPort,
) : Controller() {
    @ProtectedRoute("firebase")
    @Get("/api/user/statistics")
    suspend fun getStatistics(call: ApplicationCall) {
        val userId = fromUid(call)
        val statistics = userStatisticsUseCase.getUserRating(userId) ?: return call.respond(HttpStatusCode.NotFound)
        call.respond(statistics.toResponse())
    }

    @ProtectedRoute("firebase")
    @Post("/api/user/settings/request-promotion")
    suspend fun requestPromotion(call: ApplicationCall) {
        val userId = fromUid(call)
        userSettingsUseCase.requestPromotion(userId).fold(
            { call.respond(it) },
            { call.respond(it) },
        )
    }

    @ProtectedRoute("firebase")
    @Post("/api/user/settings/change-language")
    suspend fun changeLanguage(call: ApplicationCall) {
        val userId = fromUid(call)
        val newLanguage = call.receive<String>()
        userSettingsUseCase.update(UserSettingsCommand.Update.Language(userId, newLanguage))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("firebase")
    @Post("/api/user/settings/change-push-notifications")
    suspend fun changePushNotifications(call: ApplicationCall) {
        val userId = fromUid(call)
        val newPushNotifications = call.receive<Boolean>()
        userSettingsUseCase.update(UserSettingsCommand.Update.PushNotification(userId, newPushNotifications))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("firebase")
    @Post("/api/user/settings/change-public-visibility")
    suspend fun changePublicVisibility(call: ApplicationCall) {
        val userId = fromUid(call)
        val newPublicVisibility = call.receive<Boolean>()
        userSettingsUseCase.update(UserSettingsCommand.Update.PublicVisibility(userId, newPublicVisibility))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("firebase")
    @Get("/api/user/settings")
    suspend fun getUserSettings(call: ApplicationCall) {
        val userId = fromUid(call)
        return userSettingsUseCase.get(userId).fold(
            { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
            { call.respond(HttpStatusCode.OK, it) },
        )
    }
}
