package io.critica.presentation.controller

import io.critica.application.game.CreateGameRequest
import io.critica.application.player.PlayerResponse
import io.critica.domain.*
import io.critica.presentation.action.game.Event
import io.critica.presentation.action.game.Game
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class GameController(
    private val event: Event,
    private val game: Game,
) {
    fun Routing.gameRoutes() {
            route("/game") {
                get("/list") {
                    val games = game.list()
                    call.respond(games)
                }

                post("/create") {
                    val date = call.receive<String>()
                    val game = game.create(CreateGameRequest(date))
                    call.respond(game)
                }

                post("/start{id}") {
                    val gameId = call.parameters["id"]?.toInt()
                    if (gameId == null) {
                        call.respondText("Invalid ID")
                        return@post
                    }
                    val game = game.start(gameId)
                    call.respond(game)
                }

                put ("/{id}/addPlayer") {
                    val gameId = call.receiveParameters()["id"]?.toInt()
                    val playerName = call.receiveParameters()["playerName"].toString()
//                    game.addPlayer(gameId!!, playerName)

                    call.respond(204)
                }

                get("/get/{id}") {
                    val id = call.parameters["id"]?.toInt()
                    if (id == null) {
                        call.respondText("Invalid ID")
                        return@get
                    }

                    val game = game.get(id)
                    call.respond(game)
                }

                post("/finish/{id}") {
                    val gameId = call.parameters["id"]?.toInt()
                    val winner = call.receive<String>()
                    val game = game.finish(gameId!!, Role.valueOf(winner))
                    call.respond(game)
                }
            }
        }

    companion object {
        fun Player.toResponse() = PlayerResponse(id = id.value, name = name, alive = status == "alive")
    }
}