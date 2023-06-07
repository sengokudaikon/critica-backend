package net.critika.e2e.user

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class UserControllerTest {
    @Test
    fun testGetApiUserSettings() = testApplication {
        application {
            main()
        }
        client.get("/api/user/settings").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsChangelanguage() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/change-language").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsChangepassword() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/change-password").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsChangepublicvisibility() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/change-public-visibility").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsChangepushnotifications() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/change-push-notifications").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsChangeusername() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/change-username").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsRequestpromotion() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/request-promotion").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiUserSettingsVerifyemail() = testApplication {
        application {
            main()
        }
        client.post("/api/user/settings/verify-email").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiUserStatistics() = testApplication {
        application {
            main()
        }
        client.get("/api/user/statistics").apply {
            "+"
        }
    }
}
