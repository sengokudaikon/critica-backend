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
import net.critika.application.user.query.UserEmailQuery
import net.critika.application.user.query.UserNameQuery
import net.critika.application.user.query.UserPasswordQuery
import net.critika.infrastructure.getUserId
import net.critika.infrastructure.validation.validate
import net.critika.usecase.user.UserSettingsUseCase
import net.critika.usecase.user.UserStatisticsUseCase

@RouteController
@Tag(name = "User")
class UserController(
    private val userSettingsUseCase: UserSettingsUseCase,
    private val userStatisticsUseCase: UserStatisticsUseCase,
) {
    @ProtectedRoute("jwt-user-provider")
    @Get("/api/user/statistics")
    suspend fun getStatistics(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val statistics = userStatisticsUseCase.getUserRating(userId) ?: return call.respond(HttpStatusCode.NotFound)
        call.respond(statistics.toResponse())
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/change-username")
    suspend fun changeUsername(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newUsername = call.receive<UserNameQuery>()
        validate(newUsername)
        userSettingsUseCase.changeUsername(userId, newUsername.username)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun changeEmail(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newEmail = call.receive<UserEmailQuery>()
        validate(newEmail)
        userSettingsUseCase.changeEmail(userId, newEmail.email)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/change-password")
    suspend fun changePassword(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPassword = call.receive<UserPasswordQuery>()
        validate(newPassword)
        userSettingsUseCase.changePassword(userId, newPassword.password)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/verify-email")
    suspend fun verifyEmail(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val code = call.receive<String>()
        userSettingsUseCase.verifyEmail(userId, code)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/request-promotion")
    suspend fun requestPromotion(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val user = userSettingsUseCase.requestPromotion(userId)
        call.respond(HttpStatusCode.OK, user)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/change-language")
    suspend fun changeLanguage(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newLanguage = call.receive<String>()
        userSettingsUseCase.changeLanguage(userId, newLanguage)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/change-push-notifications")
    suspend fun changePushNotifications(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPushNotifications = call.receive<Boolean>()
        userSettingsUseCase.changePushNotifications(userId, newPushNotifications)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/user/settings/change-public-visibility")
    suspend fun changePublicVisibility(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val newPublicVisibility = call.receive<Boolean>()
        userSettingsUseCase.changePublicVisibility(userId, newPublicVisibility)
        call.respond(HttpStatusCode.OK)
    }

    @ProtectedRoute("jwt-user-provider")
    @Get("/api/user/settings")
    suspend fun getUserSettings(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        return userSettingsUseCase.getUserSettings(userId).fold(
            { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
            { call.respond(HttpStatusCode.OK, it) },
        )
    }
}
