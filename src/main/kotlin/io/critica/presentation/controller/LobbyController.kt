package io.critica.presentation.controller

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.lobby.request.CreateLobby
import io.critica.usecase.lobby.LobbyCrud
import io.critica.usecase.lobby.LobbyUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.koin.core.annotation.Single
import java.util.*

@RouteController
@Single
class LobbyController(
    private val useCase: LobbyUseCase,
    private val crud: LobbyCrud
) {
    @Post("api/lobby/create")
    suspend fun createLobby(call: ApplicationCall) {
        val request = call.receive<CreateLobby>()
        val date = DateTime.parse(request.date)
        if (date.isBeforeNow) {
            call.respond(HttpStatusCode.BadRequest, "Date must be in the future or now.")
            return
        }

        // Validate that name is not empty
        val name = request.name
        if (name.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Name must not be empty.")
            return
        }
        val lobby = crud.create(request)

        call.respond(lobby)
    }

    @Get("api/lobby/{id}")
    suspend fun getLobby(call: ApplicationCall) {
            val id = call.receiveParameters()["id"]
            if (id == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return
            }

            val lobby = crud.get(UUID.fromString(id))
            call.respond(lobby)
    }

    @Get("api/lobby/list")
    suspend fun listLobbies(call: ApplicationCall) {
            val lobbies = crud.list()
            call.respond(lobbies)
    }

    @Put("api/lobby/{id}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
            val id = call.receiveParameters()["id"]

            if (id != null) {
                crud.delete(UUID.fromString(id))

                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            }
    }
    @Put("api/lobby/{id}/addGame")
    suspend fun addGame(call: ApplicationCall) {
            val id = call.receiveParameters()["id"]
            if (id == null ) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return
            }

            val time = call.request.queryParameters["time"]

            val localTime = time?.let { LocalTime.parse(it) } ?: LocalTime.now()

            val lobbyWithGame = useCase.addGame(UUID.fromString(id), localTime)
            lobbyWithGame.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
    }

    @Put("api/lobby/{id}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
            val id = call.receiveParameters()["id"]
            if (id == null ) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return
            }

            val gameId = call.receiveParameters()["gameId"]

            if (gameId == null ) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return
            }

            val lobbyWithoutGame = useCase.removeGame(UUID.fromString(id), UUID.fromString(gameId))
            lobbyWithoutGame.fold(
                { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
                { call.respond(it) }
            )
    }

    @Get("api/lobby/{id}/games")
    suspend fun getGames(call: ApplicationCall) {
            val id = call.receiveParameters()["id"]
            if (id == null ) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return
            }

            val games = useCase.getGames(UUID.fromString(id))
            call.respond(games)
    }
    @Put("api/lobby/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]
        val playerName = call.request.queryParameters["playerName"]
        if (id == null || playerName == null) {
            call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            return
        }

        val lobby = useCase.addPlayer(UUID.fromString(id), playerName)
        lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
    }

    @Put("api/lobby/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]
        val playerId = call.receiveParameters()["playerId"]
        if (id == null || playerId == null) {
            call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            return
        }

        val lobby = useCase.addPlayerById(UUID.fromString(id), UUID.fromString(playerId))
        lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
    }

    @Put("api/lobby/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]
        val playerName = call.request.queryParameters["playerName"]
        if (id == null || playerName == null) {
            call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            return
        }

        val lobby = useCase.removePlayer(UUID.fromString(id), playerName)
        lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
    }

    @Put("api/lobby/{id}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]
        val playerId = call.receiveParameters()["playerId"]
        if (id == null || playerId == null) {
            call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            return
        }

        val lobby = useCase.removePlayerById(UUID.fromString(id), UUID.fromString(playerId))
        lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
    }

    @Get("api/lobby/{id}/players")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]
        if (id == null) {
            call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            return
        }

        val players = useCase.getPlayers(UUID.fromString(id))
        call.respond(players)
    }
}
