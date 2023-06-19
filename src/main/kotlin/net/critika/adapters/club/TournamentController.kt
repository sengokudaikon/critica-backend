package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.uuid.UUID
import net.critika.adapters.Controller
import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.tournament.command.TournamentCommand
import net.critika.application.tournament.query.TournamentQuery
import net.critika.domain.user.model.UserRole
import net.critika.ports.lobby.LobbyCrudPort
import net.critika.ports.tournament.TournamentCrudPort
import net.critika.ports.tournament.TournamentLobbyPort
import net.critika.ports.tournament.TournamentPlayerPort
import java.time.LocalDateTime

@RouteController
@Tag(name = "Tournament")
class TournamentController(
    private val lobbyPort: TournamentLobbyPort,
    private val playerPort: TournamentPlayerPort,
    private val crud: TournamentCrudPort,
    private val lobbyCrud: LobbyCrudPort,
) : Controller() {
    @ProtectedRoute("firebase")
    @Get("api/tournament/{id}/members")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val players = playerPort.getPlayers(id)
        call.respond(players)
    }

    @ProtectedRoute("firebase")
    @Get("api/tournament/{id}/rating")
    suspend fun getTournamentRating(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val players = playerPort.getRating(id)
        call.respond(players)
    }

    @ProtectedRoute("firebase")
    @Get("api/tournament/{id}")
    suspend fun get(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val club = crud.get(id)
        call.respond(club)
    }

    @ProtectedRoute("firebase")
    @Get("api/tournaments/list")
    suspend fun list(call: ApplicationCall) {
        val clubs = crud.list()
        call.respond(clubs)
    }

    @ProtectedRoute("firebase")
    @Get("api/tournament/{id}/lobbies")
    suspend fun listLobbies(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val lobbies = lobbyPort.listLobbies(id)
        call.respond(lobbies)
    }

    @ProtectedRoute("firebase")
    @Get("api/tournament/{id}/games")
    suspend fun listGames(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val games = lobbyPort.listGames(id)
        call.respond(games)
    }

    @ProtectedRoute("firebase")
    @Post("api/tournament/{id}/createLobby")
    suspend fun createLobby(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
            val date = LocalDateTime.parse(call.request.queryParameters["date"])
            val creator = fromUid(call)
            val lobby = lobbyCrud.create(LobbyCommand.Create(creator, date.toString(), id))

            call.respond(lobby)
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/tournament/{id}/update")
    suspend fun update(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<TournamentCommand.Update>()
            val club = crud.update(request)
            call.respond(club)
        }
    }

    @ProtectedRoute("firebase")
    @Put("/api/tournament/{id}/join")
    suspend fun join(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("Tournament ID is required")
        val member = fromUid(call)
        val response = playerPort.join(member, id)
        call.respond(response)
    }

    @ProtectedRoute("firebase")
    @Put("/api/tournament/{id}/leave")
    suspend fun leave(call: ApplicationCall) {
        authorize(call, listOf(UserRole.USER, UserRole.HOST)) {
            val id = call.receiveParameters()["id"]?.let { TournamentQuery(UUID(it)) } ?: throw IllegalArgumentException("Tournament ID is required")
            val member = fromUid(call)
            val response = playerPort.leave(member, id.id)
            call.respond(response)
        }
    }

    @ProtectedRoute("firebase")
    @Post("/api/tournament/create")
    suspend fun create(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<TournamentCommand.Create>()
            val club = crud.create(request)
            call.respond(club)
        }
    }
}
