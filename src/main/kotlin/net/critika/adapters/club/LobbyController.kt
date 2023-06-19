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
import kotlinx.uuid.UUID
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
    @ProtectedRoute("firebase")
    @Get("api/lobby/{id}/players")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
        val players = lobbyPlayer.getPlayers(id.lobbyId)
        call.respond(players)
    }

    @ProtectedRoute("firebase")
    @Get("api/lobby/{id}")
    suspend fun getLobby(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
        val lobby = crud.get(id.lobbyId)
        call.respond(lobby)
    }

    @ProtectedRoute("firebase")
    @Get("api/lobby/list")
    suspend fun listLobbies(call: ApplicationCall) {
        val lobbies = crud.list()
        call.respond(lobbies)
    }

    @ProtectedRoute("firebase")
    @Get("api/lobby/{id}/games")
    suspend fun getGames(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
        val games = lobbyGame.getGames(id.lobbyId)
        call.respond(games)
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{id}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            crud.delete(id.lobbyId)

            call.respond(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{id}/addGame")
    suspend fun addGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
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

    @ProtectedRoute("firebase")
    @Put("api/lobby/{id}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val gameId = call.receiveParameters()["gameId"]?.let { GameQuery(UUID(it)) } ?: throw IllegalArgumentException("gameId is required")

            val lobbyWithoutGame = lobbyGame.removeGame(id.lobbyId, gameId.gameId)
            lobbyWithoutGame.fold(
                { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
                { call.respond(it) },
            )
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{lobbyId}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerName = call.request.queryParameters["playerName"] ?: throw IllegalArgumentException("playerName is required")

            val lobby = lobbyPlayer.addPlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{lobbyId}/addTemporaryPlayer")
    suspend fun addTemporaryPlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerName = call.request.queryParameters["playerName"] ?: throw IllegalArgumentException("playerName is required")

            val lobby = lobbyPlayer.addTemporaryPlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{lobbyId}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerId = call.receiveParameters()["playerId"]?.let { PlayerQuery(UUID(it)) } ?: throw IllegalArgumentException("playerId is required")
            val lobby = lobbyPlayer.addPlayerById(id.lobbyId, playerId.playerId)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{lobbyId}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerName = call.request.queryParameters["playerName"] ?: throw IllegalArgumentException("playerName is required")

            val lobby = lobbyPlayer.removePlayer(id.lobbyId, playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/lobby/{lobbyId}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        authorize(call, listOf(UserRole.HOST, UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { LobbyQuery(UUID(it)) } ?: throw IllegalArgumentException("lobbyId is required")
            val playerId = call.receiveParameters()["playerId"]?.let { PlayerQuery(UUID(it)) } ?: throw IllegalArgumentException("playerId is required")

            val lobby = lobbyPlayer.removePlayerById(id.lobbyId, playerId.playerId)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }
}
