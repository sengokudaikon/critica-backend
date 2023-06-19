package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.uuid.UUID
import net.critika.ports.club.ClubRatingPort

@RouteController
class ClubRatingController(
    private val rating: ClubRatingPort,
) {
    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating")
    suspend fun getRatingInRange(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val from = call.request.queryParameters["from"] ?: return
        val to = call.request.queryParameters["to"] ?: return
        val rating = rating.getRatingInRange(id, from, to)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/day/{day}")
    suspend fun getRatingForDay(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val day = call.parameters["day"]?.toIntOrNull() ?: return
        val rating = rating.getRatingForDay(id, day)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/year/{year}")
    suspend fun getRatingForYear(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val year = call.parameters["year"]?.toIntOrNull() ?: return
        val rating = rating.getRatingForYear(id, year)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/month/{month}")
    suspend fun getRatingForMonth(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val month = call.parameters["month"]?.toIntOrNull() ?: return
        val rating = rating.getRatingForMonth(id, month)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/season")
    suspend fun getRatingForSeason(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val rating = rating.getRatingForSeason(id)
        call.respond(rating)
    }
}
