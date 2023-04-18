package io.critica.presentation.controller

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.usecase.lobby.LobbyCrud
import io.critica.usecase.lobby.LobbyUseCase
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.joda.time.LocalTime

class LobbyController(
    private val useCase: LobbyUseCase,
    private val crud: LobbyCrud
) {
    fun Route.crud() {
        post("create") {
            val request = call.receive<CreateLobby>()
            val date = request.date
            val name = request.name
            val lobby = crud.create(CreateLobby(date, name = name))

            call.respond(lobby)
        }

        get("get/{id}") {
            val id = call.receiveParameters()["id"]?.toInt()
            if (id == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val lobby = crud.get(GetLobby(id))
            call.respond(lobby)
        }

        get("list") {
            val lobbies = crud.list()
            call.respond(lobbies)
        }

        put("delete/{id}") {
            val id = call.receiveParameters()["id"]?.toInt()

            if (id != null) {
                crud.delete(DeleteLobby(id))

                call.respond(io.ktor.http.HttpStatusCode.NoContent)
            } else {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }

    fun Route.lobbyGame() {
        put("{id}/addGame") {
            val id = call.receiveParameters()["id"]?.toInt()
            if (id == null ) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }

            val time = call.request.queryParameters["time"]

            val localTime = time?.let { LocalTime.parse(it) } ?: LocalTime.now()

            val lobbyWithGame = useCase.addGame(id, localTime)
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

            val lobbyWithoutGame = useCase.removeGame(id, gameId)
            call.respond(lobbyWithoutGame)
        }

        get("{id}/games") {
            val id = call.receiveParameters()["id"]?.toInt()
            if (id == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val games = useCase.getGames(id)
            call.respond(games)
        }
    }

    fun Route.lobbyPlayer() {
        put("{id}/addPlayer") {
            val id = call.receiveParameters()["id"]?.toInt()
            val playerName = call.request.queryParameters["playerName"]
            if (id == null || playerName == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }

            val lobby = useCase.addPlayer(id, playerName)
            call.respond(lobby)
        }

        put("{id}/addPlayer/{playerId}") {
            val id = call.receiveParameters()["id"]?.toInt()
            val playerId = call.receiveParameters()["playerId"]?.toInt()
            if (id == null || playerId == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }

            val lobby = useCase.addPlayerById(id, playerId)
            call.respond(lobby)
        }

        patch("{id}/removePlayer") {
            val id = call.receiveParameters()["id"]?.toInt()
            val playerName = call.request.queryParameters["playerName"]
            if (id == null || playerName == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@patch
            }

            val lobby = useCase.removePlayer(id, playerName)
            call.respond(lobby)
        }

        patch("{id}/removePlayer/{playerId}") {
            val id = call.receiveParameters()["id"]?.toInt()
            val playerId = call.receiveParameters()["playerId"]?.toInt()
            if (id == null || playerId == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@patch
            }

            val lobby = useCase.removePlayerById(id, playerId)
            call.respond(lobby)
        }

        get("{id}/players") {
            val id = call.receiveParameters()["id"]?.toInt()
            if (id == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val players = useCase.getPlayers(id)
            call.respond(players)
        }
    }

    fun Routing.lobbyRoutes() {
        route("api/lobby") {
            crud()
            lobbyGame()
            lobbyPlayer()
        }
    }
}
