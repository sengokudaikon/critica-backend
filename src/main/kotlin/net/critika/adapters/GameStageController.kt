package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import net.critika.application.game.query.GameQuery
import net.critika.application.stage.StageQuery
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthPrincipality
import net.critika.infrastructure.authorize
import net.critika.infrastructure.validation.validate
import net.critika.usecase.stage.StageUseCase
import java.util.*

@RouteController
class GameStageController(
    private val stageUseCase: StageUseCase,
    private val authPrincipality: AuthPrincipality,
) {
    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/bestMove/{seat}")
    suspend fun bestMove(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0

            val bestMoveParam = call.receiveParameters()["bestMove"] ?: ""
            val bestMove = bestMoveParam.split(",").mapNotNull { it.toIntOrNull() }

            val game = stageUseCase.firstShot(UUID.fromString(gameId.gameId), seat, bestMove)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addFoul/{seat}")
    suspend fun addFoul(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            stageUseCase.addFoul(UUID.fromString(gameId.gameId), seat)
            call.respond(HttpStatusCode.OK)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addBonus/{seat}")
    suspend fun addBonus(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            stageUseCase.addBonus(UUID.fromString(gameId.gameId), seat)
            call.respond(HttpStatusCode.OK)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addOPW/{seat}")
    suspend fun addOPW(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val seat = call.receiveParameters()["seat"]?.toInt() ?: 0
            val game = stageUseCase.opw(UUID.fromString(gameId.gameId), seat)
            call.respond(game.toResponse())
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/startDay/{day}")
    suspend fun startDay(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val day = call.receiveParameters()["day"]?.toInt() ?: 0
            val game = stageUseCase.startDay(UUID.fromString(gameId.gameId), day)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/startNight/{night}")
    suspend fun startNight(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val night = call.receiveParameters()["night"]?.toInt() ?: 0
            val game = stageUseCase.startNight(UUID.fromString(gameId.gameId), night)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{nightId}/shot/{seat}")
    suspend fun shot(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val shot = call.receiveParameters()["shot"]?.toInt() ?: 0
            val game = stageUseCase.setShot(UUID.fromString(gameId.gameId), UUID.fromString(nightId.id), shot)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{nightId}/detective/{seat}")
    suspend fun detective(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val detective = call.receiveParameters()["detective"]?.toInt() ?: 0
            val game = stageUseCase.setCheck(UUID.fromString(gameId.gameId), UUID.fromString(nightId.id), detective)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{nightId}/don/{seat}")
    suspend fun don(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val don = call.receiveParameters()["don"]?.toInt() ?: 0
            val game = stageUseCase.setDonCheck(UUID.fromString(gameId.gameId), UUID.fromString(nightId.id), don)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{dayId}/nominate/{candidate}")
    suspend fun addCandidate(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val game = stageUseCase.addCandidate(UUID.fromString(gameId.gameId), UUID.fromString(nightId.id), candidate)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{dayId}/remove/{candidate}")
    suspend fun removeCandidate(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val game = stageUseCase.removeCandidate(
                UUID.fromString(gameId.gameId),
                UUID.fromString(nightId.id),
                candidate,
            )
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{dayId}/vote")
    suspend fun addVote(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val nightId = call.receive<StageQuery>()
            val candidate = call.receiveParameters()["candidate"]?.toInt() ?: 0
            val vote = call.receiveParameters()["voter"]?.toInt() ?: 0
            val game =
                stageUseCase.voteOnCandidate(
                    UUID.fromString(gameId.gameId),
                    UUID.fromString(nightId.id),
                    candidate,
                    vote,
                )
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{stageId}/next")
    suspend fun nextStage(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.nextStage(UUID.fromString(gameId.gameId), UUID.fromString(stageId.id))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{stageId}/finish")
    suspend fun finishStage(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.finishStage(UUID.fromString(gameId.gameId), UUID.fromString(stageId.id))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/stage/{stageId}/prev")
    suspend fun prevStage(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val stageId = call.receive<StageQuery>()
            validate(stageId)
            val game = stageUseCase.previousStage(UUID.fromString(gameId.gameId), UUID.fromString(stageId.id))
            call.respond(game)
        }
    }
}
