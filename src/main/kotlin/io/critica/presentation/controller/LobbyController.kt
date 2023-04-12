package io.critica.presentation.controller

import io.critica.application.lobby.request.CreateLobby
import io.critica.application.lobby.request.DeleteLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.presentation.action.lobby.Lobby
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                    call.respondText("Invalid ID")
                    return@get
                }

                val lobby = action.get(GetLobby(id))
                call.respond(lobby)
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