package net.critika.e2e.gameprocess

import io.ktor.client.request.*
import io.ktor.server.testing.*
import net.critika.main
import org.junit.jupiter.api.Test

class GameProcessControllerTest {

    @Test
    fun testPutApiGameIdStartdayDay() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/startDay/{day}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiGameIdStartnightNight() = testApplication {
        application {
            main()
        }
        client.put("/api/game/{id}/startNight/{night}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageIdBestmoveSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{id}/bestMove/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageNightidDetectiveSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{nightId}/detective/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageNightidDonSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{nightId}/don/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageStageidFinish() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{stageId}/finish").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageStageidNext() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{stageId}/next").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageDayidNominateCandidate() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{dayId}/nominate/{candidate}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageStageidPrev() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{stageId}/prev").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageDayidRemoveCandidate() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{dayId}/remove/{candidate}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageNightidShotSeat() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{nightId}/shot/{seat}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiStageDayidVote() = testApplication {
        application {
            main()
        }
        client.put("/api/stage/{dayId}/vote").apply {
            TODO("Please write your test here")
        }
    }
}