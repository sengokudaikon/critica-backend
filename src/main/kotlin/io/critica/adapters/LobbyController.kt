package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.Post
import com.github.dimitark.ktorannotations.annotations.ProtectedRoute
import com.github.dimitark.ktorannotations.annotations.Put
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.common.query.TimeQuery
import io.critica.application.game.query.GameQuery
import io.critica.application.lobby.command.CreateLobby
import io.critica.application.lobby.query.LobbyQuery
import io.critica.application.player.query.PlayerNameQuery
import io.critica.application.player.query.PlayerQuery
import io.critica.domain.UserRole
import io.critica.infrastructure.AuthPrincipality
import io.critica.infrastructure.authorize
import io.critica.infrastructure.validation.Validator
import io.critica.usecase.lobby.LobbyCrud
import io.critica.usecase.lobby.LobbyUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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
    private val validator: jakarta.validation.Validator = Validator().init()
    @ProtectedRoute("jwt-auth-provider")
    @Get("api/lobby/{id}/players")
    suspend fun getPlayers(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        validator.validate(id)
        val players = useCase.getPlayers(UUID.fromString(id.id))
        call.respond(players)
    }

    @ProtectedRoute("jwt-auth-provider")
    @Get("api/lobby/{id}")
    suspend fun getLobby(call: ApplicationCall) {
        val id = call.receive<LobbyQuery>()
        validator.validate(id)
        val lobby = crud.get(UUID.fromString(id.id))
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
        val id = call.receive<LobbyQuery>()
        validator.validate(id)
        val games = useCase.getGames(UUID.fromString(id.id))
        call.respond(games)
    }

    @ProtectedRoute("jwt-auth-provider")
    @Post("api/lobby/create")
    suspend fun createLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val request = call.receive<CreateLobby>()
            validator.validate(request)
            val lobby = crud.create(request)

            call.respond(lobby)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/delete")
    suspend fun deleteLobby(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            crud.delete(UUID.fromString(id.id))

            call.respond(HttpStatusCode.NoContent)
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addGame")
    suspend fun addGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            val time = call.request.queryParameters["time"]

            val localTime = if (time != null) {
                validator.validate(TimeQuery(time))
                LocalTime.parse(time)
            } else LocalTime.now()

            val lobbyWithGame = useCase.addGame(UUID.fromString(id.id), localTime)
            lobbyWithGame.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removeGame/{gameId}")
    suspend fun removeGame(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            val gameId = call.receive<GameQuery>()
            validator.validate(id)
            validator.validate(gameId)
            val lobbyWithoutGame = useCase.removeGame(UUID.fromString(id.id), UUID.fromString(gameId.id))
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
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }
            validator.validate(PlayerNameQuery(playerName))

            val lobby = useCase.addPlayer(UUID.fromString(id.id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addTemporaryPlayer")
    suspend fun addTemporaryPlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)

            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }
            validator.validate(PlayerNameQuery(playerName))

            val lobby = useCase.addTemporaryPlayer(UUID.fromString(id.id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/addPlayer/{playerId}")
    suspend fun addPlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            val playerId = call.receive<PlayerQuery>()
            validator.validate(playerId)

            val lobby = useCase.addPlayerById(UUID.fromString(id.id), UUID.fromString(playerId.id))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removePlayer")
    suspend fun removePlayer(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            val playerName = call.request.queryParameters["playerName"]
            if (playerName == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@authorize
            }
            validator.validate(PlayerNameQuery(playerName))

            val lobby = useCase.removePlayer(UUID.fromString(id.id), playerName)
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }

    @ProtectedRoute("jwt-auth-provider")
    @Put("api/lobby/{id}/removePlayer/{playerId}")
    suspend fun removePlayerById(call: ApplicationCall) {
        call.authorize(listOf(UserRole.ADMIN, UserRole.OWNER), authPrincipality.userRepository) {
            val id = call.receive<LobbyQuery>()
            validator.validate(id)
            val playerId = call.receive<PlayerQuery>()
            validator.validate(playerId)

            val lobby = useCase.removePlayerById(UUID.fromString(id.id), UUID.fromString(playerId.id))
            lobby.fold({ call.respond(HttpStatusCode.BadRequest, it.localizedMessage) }, { call.respond(it) })
        }
    }
}
