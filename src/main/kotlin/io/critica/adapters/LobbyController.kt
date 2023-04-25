package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.lobby.request.CreateLobby
import io.critica.domain.UserRole
import io.critica.infrastructure.AuthPrincipality
import io.critica.infrastructure.authorize
import io.critica.usecase.lobby.LobbyCrud
import io.critica.usecase.lobby.LobbyUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@RouteController
@Single
class LobbyController(
    private val useCase: LobbyUseCase,
    private val crud: LobbyCrud
): KoinComponent {
    private val authPrincipality: AuthPrincipality by inject()

    @ProtectedRoute("jwt-auth-provider")
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

    @ProtectedRoute("jwt-auth-provider")
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

    @ProtectedRoute("jwt-auth-provider")
    @Get("api/lobby/list")
    suspend fun listLobbies(call: ApplicationCall) {
        val lobbies = crud.list()
        call.respond(lobbies)
    }

    @ProtectedRoute("jwt-auth-provider")
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

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/lobby/create")
    suspend fun createLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val request = call.receive<CreateLobby>()
            val date = DateTime.parse(request.date)
            if (date.isBeforeNow) {
                call.respond(HttpStatusCode.BadRequest, "Date must be in the future or now.")
                return@authorize
            }

            // Validate that name is not empty
            val name = request.name
            if (name.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Name must not be empty.")
                return@authorize
            }
            val lobby = crud.create(request)

            call.respond(lobby)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]

            if (id != null) {
                crud.delete(UUID.fromString(id))

                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
            }
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addGame")
    suspend fun addGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            if (id == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val time = call.request.queryParameters["time"]

            val localTime = time?.let { LocalTime.parse(it) } ?: LocalTime.now()

            val lobbyWithGame = useCase.addGame(UUID.fromString(id), localTime)
            lobbyWithGame.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            if (id == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val gameId = call.receiveParameters()["gameId"]

            if (gameId == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobbyWithoutGame = useCase.removeGame(UUID.fromString(id), UUID.fromString(gameId))
            lobbyWithoutGame.fold(
                { call.respond(HttpStatusCode.BadRequest, it.localizedMessage) },
                { call.respond(it) }
            )
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addPlayer")
    suspend fun addPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            val playerName = call.request.queryParameters["playerName"]
            if (id == null || playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.addPlayer(UUID.fromString(id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addTemporaryPlayer")
    suspend fun addTemporaryPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            val playerName = call.request.queryParameters["playerName"]
            if (id == null || playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.addTemporaryPlayer(UUID.fromString(id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            val playerId = call.receiveParameters()["playerId"]
            if (id == null || playerId == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.addPlayerById(UUID.fromString(id), UUID.fromString(playerId))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            val playerName = call.request.queryParameters["playerName"]
            if (id == null || playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.removePlayer(UUID.fromString(id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receiveParameters()["id"]
            val playerId = call.receiveParameters()["playerId"]
            if (id == null || playerId == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }

            val lobby = useCase.removePlayerById(UUID.fromString(id), UUID.fromString(playerId))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }
}
