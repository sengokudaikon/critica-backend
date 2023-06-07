package net.critika.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.critika.main
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {

    @Test
    fun testGetHealth() = testApplication {
        application {
            main()
        }
        client.get("/api/health").apply {
            assertEquals(HttpStatusCode.OK, this.status)
        }
    }
}
