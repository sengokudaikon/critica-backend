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
import net.critika.application.club.command.ClubCommand
import net.critika.application.lobby.command.LobbyCommand
import net.critika.domain.user.model.UserRole
import net.critika.ports.club.ClubCrudPort
import net.critika.ports.club.ClubLobbyPort
import net.critika.ports.club.ClubMemberPort
import net.critika.ports.lobby.LobbyCrudPort
import java.time.LocalDateTime

@RouteController
@Tag(name = "Club")
class ClubController(
    private val clubLobby: ClubLobbyPort,
    private val clubMember: ClubMemberPort,
    private val clubCrud: ClubCrudPort,
    private val lobbyCrud: LobbyCrudPort,
) : Controller() {
    @ProtectedRoute("firebase")
    @Get("api/club/{id}/members")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val players = clubMember.listMembers(id)
        call.respond(players)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}")
    suspend fun getClub(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val club = clubCrud.get(id)
        call.respond(club)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/list")
    suspend fun listClubs(call: ApplicationCall) {
        val clubs = clubCrud.list()
        call.respond(clubs)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/lobbies")
    suspend fun listLobbies(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val lobbies = clubLobby.listLobbies(id)
        call.respond(lobbies)
    }

    @ProtectedRoute("firebase")
    @Get("api/club/{id}/games")
    suspend fun listGames(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val games = clubLobby.listGames(id)
        call.respond(games)
    }

    @ProtectedRoute("firebase")
    @Post("api/club/{id}/createLobby")
    suspend fun createLobby(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
            val date = LocalDateTime.parse(call.request.queryParameters["date"])
            val creator = fromUid(call)
            val lobby = lobbyCrud.create(LobbyCommand.Create(creator, date.toString(), id))

            call.respond(lobby)
        }
    }

    @ProtectedRoute("firebase")
    @Put("api/club/{id}/update")
    suspend fun updateClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
            val request = call.receive<ClubCommand.Update>().copy(id = id)
            val club = clubCrud.update(request)
            call.respond(club)
        }
    }

    @ProtectedRoute("firebase")
    @Put("/api/club/{id}/join")
    suspend fun joinClub(call: ApplicationCall) {
        val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
        val member = fromUid(call)
        val response = clubMember.enterClub(member, id)
        call.respond(response)
    }

    @ProtectedRoute("firebase")
    @Put("/api/club/{id}/leave")
    suspend fun leaveClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.USER, UserRole.HOST)) {
            val id = call.receiveParameters()["id"]?.let { UUID(it) } ?: throw IllegalArgumentException("clubId is required")
            val member = fromUid(call)
            val response = clubMember.leaveClub(member, id)
            call.respond(response)
        }
    }

    @ProtectedRoute("firebase")
    @Post("/api/club/create")
    suspend fun createClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<ClubCommand.Create>()
            val club = clubCrud.create(request)
            call.respond(club)
        }
    }
}
