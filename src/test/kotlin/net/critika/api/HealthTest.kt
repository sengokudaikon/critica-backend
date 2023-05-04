package net.critika.api

import net.critika.main
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {

    @Test
    fun testGetHealth() = testApplication {
        application {
            main()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, this.status)
        }
    }
}