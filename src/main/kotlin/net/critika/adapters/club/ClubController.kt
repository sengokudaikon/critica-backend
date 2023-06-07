package net.critika.adapters.club

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import net.critika.adapters.Controller
import net.critika.application.club.command.ClubCommand
import net.critika.application.club.query.ClubMembershipQuery
import net.critika.application.club.query.ClubQuery
import net.critika.application.lobby.command.LobbyCommand
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.authentication.getUserId
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
    @ProtectedRoute("jwt")
    @Get("api/club/{clubId}/members")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receive<ClubQuery>()
        val players = clubMember.listMembers(id.id)
        call.respond(players)
    }

    @ProtectedRoute("jwt")
    @Get("api/club/{clubId}")
    suspend fun getClub(call: ApplicationCall) {
        val id = call.receive<ClubQuery>()
        val club = clubCrud.get(id.id)
        call.respond(club)
    }

    @ProtectedRoute("jwt")
    @Get("api/club/list")
    suspend fun listClubs(call: ApplicationCall) {
        val clubs = clubCrud.list()
        call.respond(clubs)
    }

    @ProtectedRoute("jwt")
    @Get("api/club/{clubId}/lobbies")
    suspend fun listLobbies(call: ApplicationCall) {
        val id = call.receive<ClubQuery>()
        val lobbies = clubLobby.listLobbies(id.id)
        call.respond(lobbies)
    }

    @ProtectedRoute("jwt")
    @Get("api/club/{clubId}/games")
    suspend fun listGames(call: ApplicationCall) {
        val id = call.receive<ClubQuery>()
        val games = clubLobby.listGames(id.id)
        call.respond(games)
    }

    @ProtectedRoute("jwt")
    @Post("api/club/{clubId}/createLobby")
    suspend fun createLobby(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<ClubQuery>()
            val date = LocalDateTime.parse(call.receiveParameters()["date"])
            val creator = call.getUserId()
            if (creator != null) {
                val lobby = lobbyCrud.create(LobbyCommand.Create(creator, date.toString(), request.id))

                call.respond(lobby)
            } else {
                call.respond(HttpStatusCode.BadRequest, "User not found")
            }
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/club/{clubId}/update")
    suspend fun updateClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<ClubCommand.Update>()
            val club = clubCrud.update(request)
            call.respond(club)
        }
    }

    @ProtectedRoute("jwt")
    @Put("/api/club/{clubId}/join")
    suspend fun joinClub(call: ApplicationCall) {
        val club = call.receive<ClubQuery>()
        val member = call.receive<ClubMembershipQuery>()
        val response = clubMember.enterClub(member.userId, club.id)
        call.respond(response)
    }

    @ProtectedRoute("jwt")
    @Put("/api/club/{clubId}/leave")
    suspend fun leaveClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.USER, UserRole.HOST)) {
            val club = call.receive<ClubQuery>()
            val member = call.receive<ClubMembershipQuery>()
            val response = clubMember.leaveClub(member.userId, club.id)
            call.respond(response)
        }
    }

    @ProtectedRoute("jwt")
    @Post("/api/club/create")
    suspend fun createClub(call: ApplicationCall) {
        authorize(call, listOf(UserRole.OWNER)) {
            val request = call.receive<ClubCommand.Create>()
            val club = clubCrud.create(request)
            call.respond(club)
        }
    }
}
