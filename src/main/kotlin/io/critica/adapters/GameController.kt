package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.game.CreateGameRequest
import io.critica.domain.PlayerRole
import io.critica.domain.UserRole
import io.critica.infrastructure.AuthPrincipality
import io.critica.infrastructure.authorize
import io.critica.usecase.game.GameUseCase
import io.critica.usecase.user.AuthUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.joda.time.DateTime
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@RouteController
@Single
class GameController(
    private val gameUseCase: GameUseCase
): KoinComponent {
    private val authPrincipality: AuthPrincipality by inject()
    @Get("api/game/list")
    suspend fun listGames(call: ApplicationCall) {
        val games = gameUseCase.list()
        call.respond(games)
    }

    @Get("api/game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.parameters["id"]
        if (id == null) {
            call.respondText("Invalid ID")
            return
        }

        val game = gameUseCase.get(UUID.fromString(id))
        call.respond(game)
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/create")
    suspend fun createGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val date = call.receive<String>()
            val dateTime = DateTime.parse(date)
            val game = gameUseCase.create(CreateGameRequest(dateTime))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/{id}/start")
    suspend fun startGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.parameters["id"]
            if (gameId == null) {
                call.respondText("Invalid ID")
                return@authorize
            }
            val game = gameUseCase.start(UUID.fromString(gameId))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receiveParameters()["id"]
            val playerName = call.receiveParameters()["playerName"].toString()
            if (gameId != null) {
                gameUseCase.addPlayerByName(UUID.fromString(gameId), playerName)
            } else {
                call.respondText("Invalid ID")
                return@authorize
            }

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receiveParameters()["id"]
            if (gameId == null) {
                call.respondText("Invalid ID")
                return@authorize
            }

            val playerId = call.receiveParameters()["playerId"]
            if (playerId == null) {
                call.respondText("Invalid ID")
                return@authorize
            }

            gameUseCase.addPlayerById(UUID.fromString(gameId), UUID.fromString(playerId))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receiveParameters()["id"]
            if (gameId == null) {
                call.respondText("Invalid ID")
                return@authorize
            }
            val playerId = call.receiveParameters()["playerId"]
            gameUseCase.removePlayerById(UUID.fromString(gameId), UUID.fromString(playerId))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.parameters["id"]

            if (gameId == null) {
                call.respondText("Invalid ID")
                return@authorize
            }

            val winner = call.receive<String>()
            val game = gameUseCase.finish(UUID.fromString(gameId), PlayerRole.valueOf(winner))
            call.respond(game)
        }
    }
}
