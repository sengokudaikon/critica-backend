package io.critica.presentation.controller

import io.critica.application.game.CreateGameRequest
import io.critica.domain.Role
import io.critica.usecase.game.EventUseCase
import io.critica.usecase.game.GameUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.DateTime

class GameController(
    private val gameUseCase: GameUseCase,
) {
    fun Route.crud() {
        get("list") {
            val games = gameUseCase.list()
            call.respond(games)
        }

        post("create") {
            val date = call.receive<String>()
            val dateTime = DateTime.parse(date)
            val game = gameUseCase.create(CreateGameRequest(dateTime))
            call.respond(game)
        }

        get("get/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respondText("Invalid ID")
                return@get
            }

            val game = gameUseCase.get(id)
            call.respond(game)
        }
    }

    fun Routing.gameRoutes() {
            route("api/game") {
                crud()
                post("start/{id}") {
                    val gameId = call.parameters["id"]?.toInt()
                    if (gameId == null) {
                        call.respondText("Invalid ID")
                        return@post
                    }
                    val game = gameUseCase.start(gameId)
                    call.respond(game)
                }

                put ("{id}/addPlayer") {
                    val gameId = call.receiveParameters()["id"]?.toInt()
                    val playerName = call.receiveParameters()["playerName"].toString()
                    if (gameId != null) {
                        gameUseCase.addPlayerByName(gameId, playerName)
                    }

                    call.respondNullable(HttpStatusCode.NoContent)
                }

                put("{id}/addPlayer/{playerId}") {
                    val gameId = call.receiveParameters()["id"]?.toInt()
                    if (gameId == null) {
                        call.respondText("Invalid ID")
                        return@put
                    }

                    val playerId = call.receiveParameters()["playerId"]?.toInt()
                    if (playerId == null) {
                        call.respondText("Invalid ID")
                        return@put
                    }

                    gameUseCase.addPlayerById(gameId, playerId)

                    call.respondNullable(HttpStatusCode.NoContent)
                }

                put ("{id}/removePlayer") {
                    val gameId = call.receiveParameters()["id"]?.toInt()
                    if (gameId == null) {
                        call.respondText("Invalid ID")
                        return@put
                    }
                    val playerId = call.receiveParameters()["playerId"]?.toInt()
                    playerId?.let { it1 -> gameUseCase.removePlayerById(gameId, it1) }

                    call.respondNullable(HttpStatusCode.NoContent)
                }

                post("finish/{id}") {
                    val gameId = call.parameters["id"]?.toInt()

                    if (gameId == null) {
                        call.respondText("Invalid ID")
                        return@post
                    }

                    val winner = call.receive<String>()
                    val game = gameUseCase.finish(gameId, Role.valueOf(winner))
                    call.respond(game)
                }
            }
        }
}
