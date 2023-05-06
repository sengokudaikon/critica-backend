package net.critika.adapters

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
import net.critika.application.game.query.GameQuery
import net.critika.application.player.query.PlayerNameQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.application.user.query.UserQuery
import net.critika.domain.PlayerRole
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthPrincipality
import net.critika.infrastructure.authorize
import net.critika.infrastructure.validation.validate
import net.critika.usecase.game.GameUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@RouteController
@Tag(name = "Game")
class GameController(
    private val gameUseCase: GameUseCase,
) : KoinComponent {
    private val authPrincipality: AuthPrincipality by inject()

    @Get("/api/game/list")
    suspend fun listGames(call: ApplicationCall) {
        val games = gameUseCase.list()
        call.respond(games)
    }

    @ProtectedRoute("jwt-user-provider")
    @Put("/api/game/{id}/host")
    suspend fun setHost(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            val hostId = call.receive<UserQuery>()
            validate(gameId)
            validate(hostId)
            val game = gameUseCase.assignHost(UUID.fromString(gameId.gameId), UUID.fromString(hostId.id))
            call.respond(game)
        }
    }

    @Get("/api/game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.receive<GameQuery>()

        val game = gameUseCase.get(UUID.fromString(id.gameId))
        call.respond(game)
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/game/{id}/start")
    suspend fun startGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            val game = gameUseCase.start(UUID.fromString(gameId.gameId))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt-user-provider")
    @Put("/api/game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            val playerName = call.receiveParameters()["playerName"].toString()
            validate(PlayerNameQuery(playerName))
            gameUseCase.addPlayerByName(UUID.fromString(gameId.gameId), playerName)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-user-provider")
    @Put("/api/game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validate(gameId)
            val playerId = call.receive<PlayerQuery>()
            validate(playerId)
            gameUseCase.addPlayerById(UUID.fromString(gameId.gameId), UUID.fromString(playerId.playerId))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-user-provider")
    @Put("/api/game/{id}/removePlayer/{playerId}")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            val playerId = call.receive<PlayerQuery>()
            gameUseCase.removePlayerById(UUID.fromString(gameId.gameId), UUID.fromString(playerId.playerId))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-user-provider")
    @Post("/api/game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            val winner = call.receive<String>()
            val game = gameUseCase.finish(UUID.fromString(gameId.gameId), PlayerRole.valueOf(winner))
            call.respond(game)
        }
    }
}
