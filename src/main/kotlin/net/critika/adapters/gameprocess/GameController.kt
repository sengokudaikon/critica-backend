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
import net.critika.adapters.Controller
import net.critika.application.game.query.GameQuery
import net.critika.application.player.query.PlayerNameQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.application.user.query.UserQuery
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

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/host/{hostId}")
    suspend fun setHost(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            val hostId = call.receive<UserQuery>()
            validate(gameId)
            validate(hostId)
            val game = gameUseCase.assignHost(gameId.gameId, hostId.id)
            call.respond(game)
        }
    }

    @Get("/api/game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.receive<GameQuery>()

        val game = gameUseCase.get(id.gameId)
        call.respond(game)
    }

    @ProtectedRoute("jwt")
    @Post("/api/game/{id}/start")
    suspend fun startGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            val game = gameUseCase.start(gameId.gameId)
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            val playerName = call.receiveParameters()["playerName"].toString()
            validate(PlayerNameQuery(playerName))
            gameUseCase.addPlayerByName(gameId.gameId, playerName)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val playerId = call.receive<PlayerQuery>()
            validate(playerId)
            gameUseCase.addPlayerById(gameId.gameId, playerId.playerId)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/game/{id}/removePlayer/{playerId}")
    suspend fun removePlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            val playerId = call.receive<PlayerQuery>()
            gameUseCase.removePlayerById(gameId.gameId, playerId.playerId)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt")
    @Post("/api/game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val gameId = call.receive<GameQuery>()
            val winner = call.receive<String>()
            val game = gameUseCase.finish(gameId.gameId, PlayerRole.valueOf(winner))
            call.respond(game)
        }
    }
}
