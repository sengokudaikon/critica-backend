package net.critika.adapters

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
import kotlinx.uuid.toJavaUUID
import net.critika.application.game.query.GameQuery
import net.critika.application.lobby.command.CreateLobby
import net.critika.application.lobby.query.LobbyQuery
import net.critika.application.player.query.PlayerNameQuery
import net.critika.application.player.query.PlayerQuery
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthPrincipality
import net.critika.infrastructure.authorize
import net.critika.infrastructure.getUserId
import net.critika.usecase.lobby.LobbyCrudUseCase
import net.critika.usecase.lobby.LobbyUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@RouteController
@Tag(name = "Lobby")
class LobbyController(
    private val useCase: LobbyUseCase,
    private val crud: LobbyCrudUseCase,
) : KoinComponent {
    private val authPrincipality: AuthPrincipality by inject()

    @ProtectedRoute("jwt")
    @Get("api/lobby/{lobbyId}/players")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        val players = useCase.getPlayers(id.lobbyId.toJavaUUID())
        call.respond(players)
    }

    @ProtectedRoute("jwt")
    @Get("api/lobby/{lobbyId}")
    suspend fun getLobby(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        val lobby = crud.get(id.lobbyId.toJavaUUID())
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
        val games = useCase.getGames(id.lobbyId.toJavaUUID())
        call.respond(games)
    }

    @ProtectedRoute("jwt")
    @Post("api/lobby/create")
    suspend fun createLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val request = call.receive<CreateLobby>()
            val date = LocalDateTime.parse(request.date)
            val creator = call.getUserId()
            if (creator != null) {
                val lobby = crud.create(creator, date)

                call.respond(lobby)
            } else {
                call.respond(HttpStatusCode.BadRequest, "User not found")
            }
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            crud.delete(id.lobbyId.toJavaUUID())

            call.respond(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addGame")
    suspend fun addGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val time = call.request.queryParameters["time"]

            val localTime = if (time != null) {
                LocalTime.parse(time)
            } else {
                LocalTime.now()
            }

            val lobbyWithGame = useCase.addGame(id.lobbyId.toJavaUUID(), localTime, null)
            lobbyWithGame.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val gameId = call.receive<GameQuery>()
            val lobbyWithoutGame = useCase.removeGame(id.lobbyId.toJavaUUID(), UUID.fromString(gameId.gameId))
            lobbyWithoutGame.fold(
                { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
                { call.respond(it) },
            )
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.parameters["lobbyId"].toString()
            val playerName = call.receive<PlayerNameQuery>().playerName
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.addPlayer(UUID.fromString(id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addTemporaryPlayer")
    suspend fun addTemporaryPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()

            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.addTemporaryPlayer(id.lobbyId.toJavaUUID(), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val playerId = call.receive<PlayerQuery>()

            val lobby = useCase.addPlayerById(id.lobbyId.toJavaUUID(), UUID.fromString(playerId.playerId))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.removePlayer(id.lobbyId.toJavaUUID(), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt")
    @Put("api/lobby/{lobbyId}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.HOST, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val playerId = call.receive<PlayerQuery>()

            val lobby = useCase.removePlayerById(id.lobbyId.toJavaUUID(), UUID.fromString(playerId.playerId))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }
}
