package io.critica.presentation.controller

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.presentation.action.lobby.Lobby
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalTime

class LobbyController(
    private val action: Lobby,
) {
    fun Routing.lobbyRoutes() {
        route("api/lobby") {
            post("create") {
                val request = call.receive<CreateLobby>()
                val date = request.date
                val name = request.name
                val lobby = action.create(CreateLobby(date, name = name))

                call.respond(lobby)
            }

            get("get/{id}") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }

                val lobby = action.get(GetLobby(id))
                call.respond(lobby)
            }

            put("{id}/addGame") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null ) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }

                val time = call.request.queryParameters["time"]

                val localTime = time?.let { LocalTime.parse(it) } ?: LocalTime.now()

                val game = action.addGame(id, localTime)
                call.respond(game)
            }

            patch("{id}/removeGame/{gameId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null ) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }
                val gameId = call.receiveParameters()["gameId"]?.toInt()

                gameId?.let { it1 -> action.removeGame(id, it1) }
                call.respondText("Game removed", status = io.ktor.http.HttpStatusCode.OK)
            }

            put("{id}/addPlayer") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerName = call.request.queryParameters["playerName"]
                if (id == null || playerName == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }

//                val lobby = action.addPlayer(id, playerName)
                call.respond("ok")
            }

            put("{id}/addPlayer/{playerId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerId = call.receiveParameters()["playerId"]?.toInt()
                if (id == null || playerId == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }

//                val lobby = action.addPlayerById(id, playerId)
                call.respond("ok")
            }

            patch("{id}/removePlayer") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerName = call.request.queryParameters["playerName"]
                if (id == null || playerName == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

//                val lobby = action.addPlayer(id, playerName)
                call.respond("ok")
            }

            patch("{id}/removePlayer/{playerId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerId = call.receiveParameters()["playerId"]?.toInt()
                if (id == null || playerId == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

//                val lobby = action.addPlayerById(id, playerId)
                call.respond("ok")
            }

            get("list") {
                val lobbies = action.list()
                call.respond(lobbies)
            }

            put("delete/{id}") {
                val id = call.receiveParameters()["id"]?.toInt()

                if (id != null) {
                    action.delete(DeleteLobby(id))

                    call.respond(io.ktor.http.HttpStatusCode.NoContent)
                } else {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                }

            }
        }
    }
}
