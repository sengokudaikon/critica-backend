package net.critika.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.uuid.toJavaUUID
import net.critika.application.lobby.query.LobbyQuery
import net.critika.application.player.query.PlayerNameQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.infrastructure.getUserId
import net.critika.infrastructure.validation.validate
import net.critika.usecase.player.PlayerUseCase
import java.util.*

@RouteController
@Tag(name = "Player")
class PlayerController(
    private val useCase: PlayerUseCase,
) {
    @Get("/api/player/{id}")
    suspend fun getPlayerProfile(call: ApplicationCall) {
        val id = call.receive<PlayerQuery>()
        val player = useCase.getPlayerProfile(UUID.fromString(id.playerId))
        call.respond(player)
    }

    @Get("/api/player/{name}")
    suspend fun getPlayerByName(call: ApplicationCall) {
        val name = call.receive<PlayerNameQuery>()
        val player = useCase.getPlayerProfile(name.playerName)
        return player.fold(
            { call.respond(HttpStatusCode.NotFound, it.localizedMessage) },
            { call.respond(it) },
        )
    }

    @ProtectedRoute("jwt")
    @Get("/api/player")
    suspend fun getCurrentPlayer(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val player = useCase.getPlayerByUser(userId)
        call.respond(player)
    }

    @ProtectedRoute("jwt")
    @Get("/api/player/enterLobby/{id}")
    suspend fun enterLobby(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val id = call.receive<LobbyQuery>()
        validate(id)
        val player = useCase.enterLobby(userId, id.lobbyId.toJavaUUID())
        call.respond(player)
    }

    @ProtectedRoute("jwt")
    @Get("/api/player/leaveLobby/{id}")
    suspend fun leaveLobby(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val id = call.receive<LobbyQuery>()
        validate(id)
        val player = useCase.exitLobby(userId, id.lobbyId.toJavaUUID())
        call.respond(player)
    }

    @ProtectedRoute("jwt")
    @Get("/api/player/enterGame/{id}")
    suspend fun enterGame(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val id = call.receive<LobbyQuery>()
        validate(id)
        val player = useCase.enterGame(userId, id.lobbyId.toJavaUUID())
        call.respond(player)
    }

    @ProtectedRoute("jwt")
    @Get("/api/player/leaveGame")
    suspend fun leaveGame(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val id = call.receive<LobbyQuery>()
        validate(id)
        val player = useCase.quitGame(userId)
        call.respond(player)
    }
}
