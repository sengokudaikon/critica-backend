package net.critika.e2e.gameprocess

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class LobbyControllerTest {
    @Test
    fun testGetApiLobbyLobbyid() = testApplication {
        application {
            main()
        }
        client.get("/api/lobby/{lobbyId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidAddgame() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/addGame").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidAddplayer() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/addPlayer").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidAddplayerPlayerid() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/addPlayer/{playerId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidAddtemporaryplayer() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/addTemporaryPlayer").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidDelete() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/delete").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiLobbyLobbyidGames() = testApplication {
        application {
            main()
        }
        client.get("/api/lobby/{lobbyId}/games").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiLobbyLobbyidPlayers() = testApplication {
        application {
            main()
        }
        client.get("/api/lobby/{lobbyId}/players").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidRemovegameGameid() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/removeGame/{gameId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidRemoveplayer() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/removePlayer").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiLobbyLobbyidRemoveplayerPlayerid() = testApplication {
        application {
            main()
        }
        client.put("/api/lobby/{lobbyId}/removePlayer/{playerId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiLobbyList() = testApplication {
        application {
            main()
        }
        client.get("/api/lobby/list").apply {
            TODO("Please write your test here")
        }
    }
}