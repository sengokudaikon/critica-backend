package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.application.game.query.GameQuery
import net.critika.application.lobby.query.LobbyQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.domain.user.model.UserRole
import net.critika.ports.lobby.LobbyCrudPort
import net.critika.ports.lobby.LobbyGamePort
import net.critika.ports.lobby.LobbyPlayerPort
import java.time.LocalTime

@RouteController
@Tag(name = "Lobby")
class LobbyController(
    private val lobbyPlayer: LobbyPlayerPort,
    private val lobbyGame: LobbyGamePort,
    private val crud: LobbyCrudPort,
) : Controller() {
    @ProtectedRoute("jwt")
    @Get("api/lobby/{lobbyId}/players")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        val players = lobbyPlayer.getPlayers(id.lobbyId)
        call.respond(players)
    }

    @ProtectedRoute("jwt")
    @Get("api/lobby/{lobbyId}")
    suspend fun getLobby(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        val lobby = crud.get(id.lobbyId)
        call.respond(lobby)
    }

    @ProtectedRoute("jwt")
    @Get("api/lobby/list")
    suspend fun listLobbies(call: ApplicationCall) {
        val lobbies = crud.list()
        call.respond(lobbies)
    }

    @ProtectedRoute("jwt")
    @Get("api/lobby/{lobbyId}/games")
    suspend fun getGames(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        val games = lobbyGame.getGames(id.lobbyId)
        call.respond(games)
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            crud.delete(id.lobbyId)

            call.respond(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addGame")
    suspend fun addGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val time = call.request.queryParameters["time"]

            val localTime = if (time != null) {
                LocalTime.parse(time)
            } else {
                LocalTime.now()
            }

            val lobbyWithGame = lobbyGame.addGame(id.lobbyId, localTime, null)
            lobbyWithGame.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val gameId = call.receive<GameQuery>()
            val lobbyWithoutGame = lobbyGame.removeGame(id.lobbyId, gameId.gameId)
            lobbyWithoutGame.fold(
                { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
                { call.respond(it) },
            )
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = lobbyPlayer.addPlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addTemporaryPlayer")
    suspend fun addTemporaryPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()

            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = lobbyPlayer.addTemporaryPlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val playerId = call.receive<PlayerQuery>()

            val lobby = lobbyPlayer.addPlayerById(id.lobbyId, playerId.playerId)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = lobbyPlayer.removePlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receive<LobbyQuery>()
            val playerId = call.receive<PlayerQuery>()

            val lobby = lobbyPlayer.removePlayerById(id.lobbyId, playerId.playerId)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }
}
