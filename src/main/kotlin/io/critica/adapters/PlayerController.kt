package io.critica.adapters

import com.github.dimitark.ktorannotations.annotations.Get
import com.github.dimitark.ktorannotations.annotations.RouteController
import io.critica.application.player.query.PlayerNameQuery
import io.critica.application.player.query.PlayerQuery
import io.critica.usecase.player.PlayerUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.swagger.v3.oas.annotations.tags.Tag
import org.koin.core.annotation.Single
import java.util.*

@RouteController
@Single
@Tag(name = "Player")
class PlayerController(
    private val useCase: PlayerUseCase,
) {
    @Get("/api/player/{id}")
    suspend fun getPlayerProfile(call: ApplicationCall)
    {
        val id = call.receive<PlayerQuery>()
        val player = useCase.getPlayerProfile(UUID.fromString(id.id))
        call.respond(player)
    }

    @Get("/api/player/{name}")
    suspend fun getPlayerByName(call: ApplicationCall)
    {
        val name = call.receive<PlayerNameQuery>()
        val player = useCase.getPlayerProfile(name.name)
        return player.fold(
            { call.respond(HttpStatusCode.NotFound, it.localizedMessage) },
            { call.respond(it) }
        )
    }

    @Get("/api/player")
    suspend fun getCurrentPlayer(call: ApplicationCall)
    {
        val player = useCase.getPlayerProfile(UUID.fromString(call.principal<UserIdPrincipal>()!!.name))
        call.respond(player)
    }
}
