package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.critika.usecase.user.UserStatisticsUseCase

@RouteController
class StatisticsController(
    private val userStatisticsUseCase: UserStatisticsUseCase,
) {
    @Get("/statistics/rating/week/{week}")
    suspend fun getRatingForWeek(call: ApplicationCall) {
        val week = call.parameters["week"]?.toIntOrNull() ?: return
        val rating = userStatisticsUseCase.getRatingForWeek(week)
        call.respond(rating)
    }

    @Get("/statistics/rating/day/{day}")
    suspend fun getRatingForDay(call: ApplicationCall) {
        val day = call.parameters["day"]?.toIntOrNull() ?: return
        val rating = userStatisticsUseCase.getRatingForDay(day)
        call.respond(rating)
    }

    @Get("/statistics/rating/year/{year}")
    suspend fun getRatingForYear(call: ApplicationCall) {
        val year = call.parameters["year"]?.toIntOrNull() ?: return
        val rating = userStatisticsUseCase.getRatingForYear(year)
        call.respond(rating)
    }

    @Get("/statistics/rating/month/{month}")
    suspend fun getRatingForMonth(call: ApplicationCall) {
        val month = call.parameters["month"]?.toIntOrNull() ?: return
        val rating = userStatisticsUseCase.getRatingForMonth(month)
        call.respond(rating)
    }

    @Get("/statistics/rating/season")
    suspend fun getRatingForSeason(call: ApplicationCall) {
        val rating = userStatisticsUseCase.getRatingForSeason()
        call.respond(rating)
    }
}
