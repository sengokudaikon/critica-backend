package io.critica

import io.critica.config.AppConfig
import io.critica.di.*
import io.critica.presentation.controller.LobbyController
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::main)
        .start(wait = true)

}

fun Application.main() {
    install(Koin) {
        modules(appModule)
    }

    val config: AppConfig by inject()
//    configureSecurity(config.jwtConfig)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    DatabaseFactory.init(dbConfig = config.dbConfig)
    configureSockets()
    configureRouting()

    val lobbyController: LobbyController by inject()
    routing {
        route("/api") {
            this@routing.lobbyRoutes(lobbyController)
        }
    }
}