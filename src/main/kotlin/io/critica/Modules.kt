package io.critica

import io.critica.plugins.*
import io.critica.presentation.Routes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureSockets()
    configureRouting()
    routing {
        Routes()
    }
}