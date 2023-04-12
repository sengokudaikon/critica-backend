package io.critica.presentation

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class Routes(
){
    fun Routing.application() {
        route("/") {
            statsRoutes()
            leaderboardRoutes()
            serviceRoutes()
        }
    }

    fun Route.statsRoutes() {
        route("/stats") {
            get("/get/{id}") {
            }
        }
    }

    fun Route.leaderboardRoutes() {
        get("/get") {
        }
    }

    fun Route.serviceRoutes() {
        route("/service") {
            get("/health") {
                call.respondText("OK")
            }

            get("/") {
                call.respondText("Critica is alive", ContentType.Text.Html)
            }

            val testStorage = mutableListOf<Test>()
            get("/test/{id}") {
                val id = call.parameters["id"]
                val customer: Test = testStorage.find { it.id == id!!.toInt() }!!
                call.respond(customer)
            }

            post("/test") {
                val customer = call.receive<Test>()
                testStorage.add(customer)
                call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
            }
        }
    }
}

@Serializable
data class Test(val id: Int, val firstName: String, val lastName: String)