package io.critica.presentation.controller

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.usecase.lobby.LobbyUseCase
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalTime

class LobbyController(
    private val action: LobbyUseCase,
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

                val lobbyWithGame = action.addGame(id, localTime)
                call.respond(lobbyWithGame)
            }

            patch("{id}/removeGame/{gameId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null ) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

                val gameId = call.receiveParameters()["gameId"]?.toInt()

                if (gameId == null ) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

                val lobbyWithoutGame = action.removeGame(id, gameId)
                call.respond(lobbyWithoutGame)
            }

            put("{id}/addPlayer") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerName = call.request.queryParameters["playerName"]
                if (id == null || playerName == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }

                val lobby = action.addPlayer(id, playerName)
                call.respond(lobby)
            }

            put("{id}/addPlayer/{playerId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerId = call.receiveParameters()["playerId"]?.toInt()
                if (id == null || playerId == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }

                val lobby = action.addPlayerById(id, playerId)
                call.respond(lobby)
            }

            patch("{id}/removePlayer") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerName = call.request.queryParameters["playerName"]
                if (id == null || playerName == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

                val lobby = action.removePlayer(id, playerName)
                call.respond(lobby)
            }

            patch("{id}/removePlayer/{playerId}") {
                val id = call.receiveParameters()["id"]?.toInt()
                val playerId = call.receiveParameters()["playerId"]?.toInt()
                if (id == null || playerId == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@patch
                }

                val lobby = action.removePlayerById(id, playerId)
                call.respond(lobby)
            }

            get("{id}/players") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }

                val players = action.getPlayers(id)
                call.respond(players)
            }

            get("{id}/games") {
                val id = call.receiveParameters()["id"]?.toInt()
                if (id == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }

                val games = action.getGames(id)
                call.respond(games)
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
