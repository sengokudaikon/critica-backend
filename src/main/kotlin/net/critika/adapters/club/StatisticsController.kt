package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.critika.ports.club.RatingPort

@RouteController
class StatisticsController(
    private val statisticsUseCase: RatingPort,
) {
    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating")
    suspend fun getRatingInRange(call: ApplicationCall) {
        val from = call.request.queryParameters["from"] ?: return
        val to = call.request.queryParameters["to"] ?: return
        val rating = statisticsUseCase.getRatingInRange(from, to)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/week/{week}")
    suspend fun getRatingForWeek(call: ApplicationCall) {
        val week = call.parameters["week"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForWeek(week)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/day/{day}")
    suspend fun getRatingForDay(call: ApplicationCall) {
        val day = call.parameters["day"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForDay(day)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/year/{year}")
    suspend fun getRatingForYear(call: ApplicationCall) {
        val year = call.parameters["year"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForYear(year)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/month/{month}")
    suspend fun getRatingForMonth(call: ApplicationCall) {
        val month = call.parameters["month"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForMonth(month)
        call.respond(rating)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/rating/season")
    suspend fun getRatingForSeason(call: ApplicationCall) {
        val rating = statisticsUseCase.getRatingForSeason()
        call.respond(rating)
    }
}
