package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.critika.usecase.stats.StatisticsUseCase

@RouteController
class StatisticsController(
    private val statisticsUseCase: StatisticsUseCase,
) {
    @ProtectedRoute("jwt-user-provider")
    @Get("api/statistics/rating/week/{week}")
    suspend fun getRatingForWeek(call: ApplicationCall) {
        val week = call.parameters["week"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForWeek(week)
        call.respond(rating)
    }

    @ProtectedRoute("jwt-user-provider")
    @Get("api/statistics/rating/day/{day}")
    suspend fun getRatingForDay(call: ApplicationCall) {
        val day = call.parameters["day"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForDay(day)
        call.respond(rating)
    }

    @ProtectedRoute("jwt-user-provider")
    @Get("api/statistics/rating/year/{year}")
    suspend fun getRatingForYear(call: ApplicationCall) {
        val year = call.parameters["year"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForYear(year)
        call.respond(rating)
    }

    @ProtectedRoute("jwt-user-provider")
    @Get("api/statistics/rating/month/{month}")
    suspend fun getRatingForMonth(call: ApplicationCall) {
        val month = call.parameters["month"]?.toIntOrNull() ?: return
        val rating = statisticsUseCase.getRatingForMonth(month)
        call.respond(rating)
    }

    @ProtectedRoute("jwt-user-provider")
    @Get("api/statistics/rating/season")
    suspend fun getRatingForSeason(call: ApplicationCall) {
        val rating = statisticsUseCase.getRatingForSeason()
        call.respond(rating)
    }
}
