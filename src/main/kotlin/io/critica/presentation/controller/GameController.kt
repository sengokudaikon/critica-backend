package io.critica.presentation.controller

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.game.CreateGameRequest
import io.critica.domain.PlayerRole
import io.critica.usecase.game.GameUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.joda.time.DateTime
import org.koin.core.annotation.Single
import java.util.*

@RouteController
@Single
class GameController(
    private val gameUseCase: GameUseCase,
) {
    @Get("game/list")
    suspend fun listGames(call: ApplicationCall) {
        val games = gameUseCase.list()
        call.respond(games)
    }

    @Post("game/create")
    suspend fun createGame(call: ApplicationCall) {
        val date = call.receive<String>()
        val dateTime = DateTime.parse(date)
        val game = gameUseCase.create(CreateGameRequest(dateTime))
        call.respond(game)
    }

    @Get("game/{id}")
    suspend fun getGame(call: ApplicationCall) {
        val id = call.parameters["id"]
        if (id == null) {
            call.respondText("Invalid ID")
            return
        }

        val game = gameUseCase.get(UUID.fromString(id))
        call.respond(game)
    }


    @Post("game/{id}/start")
    suspend fun startGame(call: ApplicationCall){
        val gameId = call.parameters["id"]
        if (gameId == null) {
            call.respondText("Invalid ID")
            return
        }
        val game = gameUseCase.start(UUID.fromString(gameId))
        call.respond(game)
    }

    @Put("game/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        val gameId = call.receiveParameters()["id"]
        val playerName = call.receiveParameters()["playerName"].toString()
        if (gameId != null) {
            gameUseCase.addPlayerByName(UUID.fromString(gameId), playerName)
        }

        call.respondNullable(HttpStatusCode.NoContent)
    }

    @Put("game/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        val gameId = call.receiveParameters()["id"]
        if (gameId == null) {
            call.respondText("Invalid ID")
            return
        }

        val playerId = call.receiveParameters()["playerId"]
        if (playerId == null) {
            call.respondText("Invalid ID")
            return
        }

        gameUseCase.addPlayerById(UUID.fromString(gameId), UUID.fromString(playerId))

        call.respondNullable(HttpStatusCode.NoContent)
    }

    @Put("game/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        val gameId = call.receiveParameters()["id"]
        if (gameId == null) {
            call.respondText("Invalid ID")
            return
        }
        val playerId = call.receiveParameters()["playerId"]
        gameUseCase.removePlayerById(UUID.fromString(gameId), UUID.fromString(playerId))

        call.respondNullable(HttpStatusCode.NoContent)
    }

    @Post("game/{id}/finish")
    suspend fun finishGame(call: ApplicationCall) {
        val gameId = call.parameters["id"]

        if (gameId == null) {
            call.respondText("Invalid ID")
            return
        }

        val winner = call.receive<String>()
        val game = gameUseCase.finish(UUID.fromString(gameId), PlayerRole.valueOf(winner))
        call.respond(game)
    }
}
