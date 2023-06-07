package net.critika.e2e.user

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class AdminControllerTest {
    @Test
    fun testPostApiAdminUsersUseridPromote() = testApplication {
        application {
            main()
        }
        client.post("/api/admin/users/{userId}/promote").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiAdminUsersUseridReject() = testApplication {
        application {
            main()
        }
        client.post("/api/admin/users/{userId}/reject").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiAdminUsersRequestingpromotion() = testApplication {
        application {
            main()
        }
        client.get("/api/admin/users/requesting-promotion").apply {
            TODO("Please write your test here")
        }
    }
}
