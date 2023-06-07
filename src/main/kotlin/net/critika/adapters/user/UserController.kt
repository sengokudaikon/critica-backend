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
import net.critika.application.user.command.UserSettingsCommand
import net.critika.application.user.query.UserEmailQuery
import net.critika.application.user.query.UserNameQuery
import net.critika.application.user.query.UserPasswordQuery
import net.critika.infrastructure.authentication.getUserId
import net.critika.infrastructure.validation.validate
import net.critika.ports.user.UserRatingPort
import net.critika.ports.user.UserSettingsPort

@RouteController
@Tag(name = "User")
class UserController(
    private val userSettingsUseCase: UserSettingsPort,
    private val userStatisticsUseCase: UserRatingPort,
) {
    @ProtectedRoute("jwt")
    @Get("/api/user/statistics")
    suspend fun getStatistics(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val statistics = userStatisticsUseCase.getUserRating(userId) ?: return call.respond(HttpStatusCode.NotFound)
        call.respond(statistics.toResponse())
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/change-username")
    suspend fun changeUsername(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newUsername = call.receive<UserNameQuery>()
        validate(newUsername)
        userSettingsUseCase.update(UserSettingsCommand.Update.Username(userId, newUsername.username))
        call.respond(HttpStatusCode.OK)
    }

    suspend fun changeEmail(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newEmail = call.receive<UserEmailQuery>()
        validate(newEmail)
        userSettingsUseCase.update(UserSettingsCommand.Update.Email(userId, newEmail.email))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/change-password")
    suspend fun changePassword(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPassword = call.receive<UserPasswordQuery>()
        validate(newPassword)
        userSettingsUseCase.update(UserSettingsCommand.Update.Password(userId, newPassword.password))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/verify-email")
    suspend fun verifyEmail(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val code = call.receive<String>()
        userSettingsUseCase.verifyEmail(userId, code)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/request-promotion")
    suspend fun requestPromotion(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val user = userSettingsUseCase.requestPromotion(userId)
        call.respond(HttpStatusCode.OK, user)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/change-language")
    suspend fun changeLanguage(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newLanguage = call.receive<String>()
        userSettingsUseCase.update(UserSettingsCommand.Update.Language(userId, newLanguage))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/change-push-notifications")
    suspend fun changePushNotifications(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPushNotifications = call.receive<Boolean>()
        userSettingsUseCase.update(UserSettingsCommand.Update.PushNotification(userId, newPushNotifications))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Post("/api/user/settings/change-public-visibility")
    suspend fun changePublicVisibility(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPublicVisibility = call.receive<Boolean>()
        userSettingsUseCase.update(UserSettingsCommand.Update.PublicVisibility(userId, newPublicVisibility))
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt")
    @Get("/api/user/settings")
    suspend fun getUserSettings(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        return userSettingsUseCase.get(userId).fold(
            { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
            { call.respond(HttpStatusCode.OK, it) },
        )
    }
}
