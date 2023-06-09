package net.critika.adapters.gameprocess

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.application.lobby.query.LobbyQuery
import net.critika.application.player.query.PlayerNameQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.application.player.usecase.PlayerUseCase
import net.critika.infrastructure.validation.validate

@RouteController
@Tag(name = "Player")
class PlayerController(
    private val useCase: PlayerUseCase,
) : Controller() {
    @Get("/api/player/{id}")
    suspend fun getPlayerProfile(call: ApplicationCall) {
        val id = call.receive<PlayerQuery>()
        val player = useCase.getPlayerProfile(id.playerId)
        call.respond(player.toResponse())
    }

    @Get("/api/player/{name}")
    suspend fun getPlayerByName(call: ApplicationCall) {
        val name = call.receive<PlayerNameQuery>()
        val player = useCase.getPlayerProfile(name.playerName)
        return player.fold(
            { call.respond(HttpStatusCode.NotFound, it.localizedMessage) },
            { call.respond(it.toResponse()) },
        )
    }

    @ProtectedRoute("firebase")
    @Get("/api/player")
    suspend fun getCurrentPlayer(call: ApplicationCall) {
        val userId = fromUid(call)
        val player = useCase.getPlayerByUser(userId)
        call.respond(player.toResponse())
    }

    @ProtectedRoute("firebase")
    @Get("/api/player/enterLobby/{id}")
    suspend fun enterLobby(call: ApplicationCall) {
        val userId = fromUid(call)
        val id = call.receive<LobbyQuery>()
        validate(id)
        useCase.enterLobby(userId, id.lobbyId).fold(
            {
                call.respond(it)
                return
            },
            {
                call.respond(it)
            },
        )
    }

    @ProtectedRoute("firebase")
    @Get("/api/player/leaveLobby/{id}")
    suspend fun leaveLobby(call: ApplicationCall) {
        val userId = fromUid(call)
        val id = call.receive<LobbyQuery>()
        validate(id)
        useCase.exitLobby(userId, id.lobbyId).fold(
            {
                call.respond(it)
                return
            },
            {
                call.respond(it)
            },
        )
    }

    @ProtectedRoute("firebase")
    @Get("/api/player/enterGame/{id}")
    suspend fun enterGame(call: ApplicationCall) {
        val userId = fromUid(call)
        val id = call.receive<LobbyQuery>()
        validate(id)
        useCase.enterGame(userId, id.lobbyId).fold(
            {
                call.respond(it)
                return
            },
            {
                call.respond(it)
            },
        )
    }

    @ProtectedRoute("firebase")
    @Get("/api/player/leaveGame")
    suspend fun leaveGame(call: ApplicationCall) {
        val userId = fromUid(call)
        val id = call.receive<LobbyQuery>()
        validate(id)
        useCase.quitGame(userId).fold(
            {
                call.respond(it)
                return
            },
            {
                call.respond(it)
            },
        )
    }
}
