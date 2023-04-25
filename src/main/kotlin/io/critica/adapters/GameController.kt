package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.common.query.DateQuery
import io.critica.application.game.command.CreateGame
import io.critica.application.game.query.GameQuery
import io.critica.application.player.query.PlayerNameQuery
import io.critica.application.player.query.PlayerQuery
import io.critica.domain.PlayerRole
import io.critica.domain.UserRole
import io.critica.infrastructure.AuthPrincipality
import io.critica.infrastructure.validation.Validator
import io.critica.infrastructure.authorize
import io.critica.usecase.game.GameUseCase
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
    private val validator: jakarta.validation.Validator = Validator().init()
    @Get("api/game/list")
    suspend fun listGames(call: ApplicationCall) {
        val games = gameUseCase.list()
        call.respond(games)
    }

    @Get("api/game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.receive<GameQuery>()
        validator.validate(id)

        val game = gameUseCase.get(UUID.fromString(id.id))
        call.respond(game)
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/create")
    suspend fun createGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val date = call.receive<DateQuery>()
            validator.validate(date)
            val dateTime = DateTime.parse(date.date)
            val game = gameUseCase.create(CreateGame(dateTime))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/{id}/start")
    suspend fun startGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validator.validate(gameId)
            val game = gameUseCase.start(UUID.fromString(gameId.id))
            call.respond(game)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validator.validate(gameId)
            val playerName = call.receiveParameters()["playerName"].toString()
            validator.validate(PlayerNameQuery(playerName))
            gameUseCase.addPlayerByName(UUID.fromString(gameId.id), playerName)

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validator.validate(gameId)
            val playerId = call.receive<PlayerQuery>()

            gameUseCase.addPlayerById(UUID.fromString(gameId.id), UUID.fromString(playerId.id))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/game/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validator.validate(gameId)
            val playerId = call.receive<PlayerQuery>()
            validator.validate(playerId)
            gameUseCase.removePlayerById(UUID.fromString(gameId.id), UUID.fromString(playerId.id))

            call.respondNullable(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val gameId = call.receive<GameQuery>()
            validator.validate(gameId)
            val winner = call.receive<String>()
            val game = gameUseCase.finish(UUID.fromString(gameId.id), PlayerRole.valueOf(winner))
            call.respond(game)
        }
    }
}
