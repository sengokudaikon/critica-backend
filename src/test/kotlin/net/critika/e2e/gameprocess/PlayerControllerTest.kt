package net.critika.e2e.gameprocess

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class PlayerControllerTest {

    @Test
    fun testGetApiPlayer() = testApplication {
        application {
            main()
        }
        client.get("/api/player").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerId() = testApplication {
        application {
            main()
        }
        client.get("/api/player/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerName() = testApplication {
        application {
            main()
        }
        client.get("/api/player/{name}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerEntergameId() = testApplication {
        application {
            main()
        }
        client.get("/api/player/enterGame/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerEnterlobbyId() = testApplication {
        application {
            main()
        }
        client.get("/api/player/enterLobby/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerLeavegame() = testApplication {
        application {
            main()
        }
        client.get("/api/player/leaveGame").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiPlayerLeavelobbyId() = testApplication {
        application {
            main()
        }
        client.get("/api/player/leaveLobby/{id}").apply {
            TODO("Please write your test here")
        }
    }
}