package io.critica

import com.codahale.metrics.Slf4jReporter
import io.critica.config.AppConfig
import io.critica.di.DatabaseFactory
import io.critica.di.appModule
import io.critica.presentation.controller.LobbyController
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.slf4j.event.Level
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val maxRange: Int = 10
private const val maxAgeSeconds: Int = 24 * 60 * 60
private const val duration: Long = 10L
private const val wsDuration: Long = 15L

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::main)
        .start(wait = true)

}

fun Application.main() {
    install(Koin) {
        modules(appModule)
    }

    val config: AppConfig by inject()
    val dotenv = dotenv()
    val runMigrations = dotenv["RUN_MIGRATIONS"]?.toBoolean() ?: false

    if (runMigrations) { DatabaseFactory.init(dbConfig = config.dbConfig) }
//    configureSecurity(config.jwtConfig)
    configCache()
    configHttp()
    configMonitoring()
    configRouting()
}

private fun Application.configRouting() {
    val lobbyController: LobbyController by inject()
    routing {
        webSocket("/ws") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }

        static("/static") {
            resources("static")
        }

        get("/") { call.respondRedirect("/static/index.html") }

        route("api") {
            this@routing.lobbyRoutes(lobbyController)
        }
    }
}

private fun Application.configMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
    }
    install(DropwizardMetrics) {
        Slf4jReporter.forRegistry(registry)
            .outputTo(this@configMonitoring.log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start(duration, TimeUnit.SECONDS)
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
}

private fun Application.configHttp() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        anyHost()
    }

    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP request.
        // If the HTTP request specifies more ranges, they will all be merged into a single range.
        maxRangeCount = maxRange
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(StatusPages) {
        exception<InternalError> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(wsDuration)
        timeout = Duration.ofSeconds(wsDuration)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

private fun Application.configCache() {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> io.ktor.http.content.CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = maxAgeSeconds)
                )
                else -> null
            }
        }
    }
}

fun Routing.lobbyRoutes(controller: LobbyController) {
    with(controller) {
        lobbyRoutes()
    }
}
