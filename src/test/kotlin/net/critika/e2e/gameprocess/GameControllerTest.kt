package net.critika.e2e.gameprocess

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class GameControllerTest {

    @Test
    fun testGetApiGameId() = testApplication {
        application {
            main()
        }
        client.get("/api/game/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdAddbonusSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/addBonus/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdAddfoulSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/addFoul/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdAddopwSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/addOPW/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdAddplayer() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/addPlayer").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdAddplayerPlayerid() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/addPlayer/{playerId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiGameIdFinish() = testApplication {
        application {
            main()
        }
        client.post("/api/game/{id}/finish").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdHostHostid() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/host/{hostId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdRemoveplayerPlayerid() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/removePlayer/{playerId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiGameIdStart() = testApplication {
        application {
            main()
        }
        client.post("/api/game/{id}/start").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiGameList() = testApplication {
        application {
            main()
        }
        client.get("/api/game/list").apply {
            TODO("Please write your test here")
        }
    }
}