package net.critika.adapters.gameprocess

import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import net.critika.adapters.Controller
import net.critika.application.game.query.GameQuery
import net.critika.application.stage.query.StageQuery
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.validation.validate
import net.critika.ports.stage.StagePort

@RouteController
class GameStageController(
    private val stageUseCase: StagePort,
) : Controller() {
    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addFoul/{seat}")
    suspend fun addFoul(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            stageUseCase.addFoul(gameId.gameId, seat)
            call.respond(HttpStatusCode.OK)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addBonus/{seat}")
    suspend fun addBonus(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            stageUseCase.addBonus(gameId.gameId, seat)
            call.respond(HttpStatusCode.OK)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addOPW/{seat}")
    suspend fun addOPW(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            val game = stageUseCase.opw(gameId.gameId, seat)
            call.respond(game.toResponse())
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/startDay/{day}")
    suspend fun startDay(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val day = call.receiveParameters()["day"]?.toInt() ?: 0
            val game = stageUseCase.startDay(gameId.gameId, day)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/startNight/{night}")
    suspend fun startNight(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val night = call.receiveParameters()["night"]?.toInt() ?: 0
            val game = stageUseCase.startNight(gameId.gameId, night)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{id}/bestMove/{seat}")
    suspend fun bestMove(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<StageQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0

            val bestMoveParam = call.receiveParameters()["bestMove"] ?: ""
            val bestMove = bestMoveParam.split(",").mapNotNull { it.toIntOrNull() }

            val game = stageUseCase.firstShot(gameId.id, seat, bestMove)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{nightId}/shot/{seat}")
    suspend fun shot(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val shot = call.receiveParameters()["shot"]?.toInt() ?: 0
            val game = stageUseCase.setShot(nightId.id, shot)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{nightId}/detective/{seat}")
    suspend fun detective(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val detective = call.receiveParameters()["detective"]?.toInt() ?: 0
            val game = stageUseCase.setCheck(nightId.id, detective)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{nightId}/don/{seat}")
    suspend fun don(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val don = call.receiveParameters()["don"]?.toInt() ?: 0
            val game = stageUseCase.setDonCheck(nightId.id, don)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{dayId}/nominate/{candidate}")
    suspend fun addCandidate(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val game = stageUseCase.addCandidate(nightId.id, candidate)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{dayId}/remove/{candidate}")
    suspend fun removeCandidate(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val game = stageUseCase.removeCandidate(
                nightId.id,
                candidate,
            )
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{dayId}/vote")
    suspend fun addVote(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val vote = call.receiveParameters()["voter"]?.toInt() ?: 0
            val game =
                stageUseCase.voteOnCandidate(
                    nightId.id,
                    candidate,
                    vote,
                )
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{stageId}/next")
    suspend fun nextStage(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.nextStage(stageId.id)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{stageId}/finish")
    suspend fun finishStage(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.finishStage(stageId.id)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/stage/{stageId}/prev")
    suspend fun prevStage(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.previousStage(stageId.id)
            call.respond(game)
        }
    }
}
