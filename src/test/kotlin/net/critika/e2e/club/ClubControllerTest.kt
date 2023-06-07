package net.critika.e2e.club

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class ClubControllerTest {
    @Test
    fun testGetApiClubClubid() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{clubId}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiClubClubidCreatelobby() = testApplication {
        application {
            main()
        }
        client.post("/api/club/{clubId}/createLobby").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubClubidGames() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{clubId}/games").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiClubClubidJoin() = testApplication {
        application {
            main()
        }
        client.put("/api/club/{clubId}/join").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiClubClubidLeave() = testApplication {
        application {
            main()
        }
        client.put("/api/club/{clubId}/leave").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubClubidLobbies() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{clubId}/lobbies").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubClubidMembers() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{clubId}/members").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRating() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRatingDayDay() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating/day/{day}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRatingMonthMonth() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating/month/{month}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRatingSeason() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating/season").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRatingWeekWeek() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating/week/{week}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubIdRatingYearYear() = testApplication {
        application {
            main()
        }
        client.get("/api/club/{id}/rating/year/{year}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiClubClubidUpdate() = testApplication {
        application {
            main()
        }
        client.put("/api/club/{clubId}/update").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiClubCreate() = testApplication {
        application {
            main()
        }
        client.post("/api/club/create").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiClubList() = testApplication {
        application {
            main()
        }
        client.get("/api/club/list").apply {
            TODO("Please write your test here")
        }
    }
}