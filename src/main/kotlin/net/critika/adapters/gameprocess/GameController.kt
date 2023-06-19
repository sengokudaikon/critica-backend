package net.critika.adapters.gameprocess

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.uuid.UUID
import net.critika.adapters.Controller
import net.critika.application.player.query.PlayerNameQuery
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.validation.validate
import net.critika.ports.game.GamePort

@RouteController
@Tag(name = "Game")
class GameController(
    private val gameUseCase: GamePort,
) : Controller() {
    @Get("/api/game/list")
    suspend fun listGames(call: ApplicationCall) {
        val games = gameUseCase.list()
        call.respond(games)
    }

    @ProtectedRoute("firebase")
    @Put("/api/game/{id}/host/{hostId}")
    suspend fun setHost(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val game = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val host = call.receiveParameters()["hostId"]?.let { UUID(it) } ?: throw IllegalArgumentException("hostId is required")

            val response = gameUseCase.assignHost(game, host)
            call.respond(response)
        }
    }

    @Get("/api/game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")

        val game = gameUseCase.get(id)
        call.respond(game)
    }

    @ProtectedRoute("firebase")
    @Post("/api/game/{id}/start")
    suspend fun startGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val game = gameUseCase.start(id)
            call.respond(game)
        }
    }

    @ProtectedRoute("firebase")
    @Put("/api/game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerName = call.receiveParameters()["playerName"].toString()
            validate(PlayerNameQuery(playerName))
            gameUseCase.addPlayerByName(gameId, playerName)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("firebase")
    @Put("/api/game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerId = call.receiveParameters()["playerId"]?.let { UUID(it) } ?: throw IllegalArgumentException("playerId is required")
            gameUseCase.addPlayerById(gameId, playerId)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("firebase")
    @Put("/api/game/{id}/removePlayer/{playerId}")
    suspend fun removePlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerId = call.receiveParameters()["playerId"]?.let { UUID(it) } ?: throw IllegalArgumentException("playerId is required")
            gameUseCase.removePlayerById(gameId, playerId)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("firebase")
    @Post("/api/game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("lobbyId is required")
            val winner = call.request.queryParameters["winner"] ?: throw IllegalArgumentException("winner is required")
            val game = gameUseCase.finish(gameId, PlayerRole.valueOf(winner))
            call.respond(game)
        }
    }
}
