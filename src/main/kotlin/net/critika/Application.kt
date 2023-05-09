package net.critika

import com.codahale.metrics.Slf4jReporter
import com.github.dimitark.ktor.routing.ktorRoutingAnnotationConfig
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
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
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import kotlinx.serialization.json.Json
import net.critika.config.AppConfig
import net.critika.di.MainModule
import net.critika.infrastructure.DatabaseFactory
import net.critika.infrastructure.Security
import net.critika.infrastructure.authentication.FirebaseAdmin
import net.critika.infrastructure.authentication.FirebasePrincipal
import net.critika.infrastructure.authentication.firebase
import net.critika.infrastructure.handleErrors
import org.koin.ksp.generated.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.slf4j.event.Level
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val MAX_RANGE: Int = 10
private const val MAX_AGE_SECONDS: Int = 24 * 60 * 60
private const val WS_DURATION: Long = 15L

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::main)
        .start(wait = true)
}

fun Application.main() {
    install(Koin) {
        modules(MainModule().module)
    }
    val dotenv = dotenv()
    val config: AppConfig by inject()
    val runMigrations = dotenv["RUN_MIGRATIONS"]?.toBoolean() ?: false

    DatabaseFactory.init(runMigrations = runMigrations, dbConfig = config.dbConfig())
    val security: Security by inject()
    FirebaseAdmin.init()
    install(Authentication) {
        firebase {
            validate { firebaseToken ->
                val userId = firebaseToken.uid
                if (userId != null) {
                    FirebasePrincipal(userId)
                } else {
                    null
                }
            }
        }
        jwt("jwt") {
            verifier(security.configureSecurity())
            realm = "critica.io"
            validate { credential ->
                val userId = credential.payload.subject
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    configCache()
    configHttp()
    configMonitoring()
    ktorRoutingAnnotationConfig()
    configRouting()
}

private fun Application.configRouting() {
    install(Resources)
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
            configLoaders
        }
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }

        static("/static") {
            resources("static")
        }

        get("/") { call.respondRedirect("/static/index.html") }

        get("/health") {
            call.respond(HttpStatusCode.OK, "Healthy")
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
            .withLoggingLevel(Slf4jReporter.LoggingLevel.INFO)
            .outputTo(this@configMonitoring.log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start(1000, TimeUnit.SECONDS)
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
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowMethods)
        allowHeader(HttpHeaders.AccessControlAllowCredentials)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.AcceptLanguage)
        allowCredentials = true
        anyHost()
    }

    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP query.
        // If the HTTP query specifies more ranges, they will all be merged into a single range.
        maxRangeCount = MAX_RANGE
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }
    handleErrors()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(WS_DURATION)
        timeout = Duration.ofSeconds(WS_DURATION)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

private fun Application.configCache() {
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> io.ktor.http.content.CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = MAX_AGE_SECONDS),
                )

                else -> null
            }
        }
    }
}
