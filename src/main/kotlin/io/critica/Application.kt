package io.critica

import io.critica.config.AppConfig
import io.critica.persistence.dao.UserTokenDao
import io.critica.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = AppConfig.load()
    configureSecurity(config.jwtConfig, UserTokenDao())
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureDatabases(config.dbConfig)
    configureMigrations(config.dbConfig)
    configureSockets()
    configureRouting()
}