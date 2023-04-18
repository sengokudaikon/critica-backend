package io.critica.usecase.game

import io.critica.persistence.repository.EventRepository
import io.critica.persistence.repository.GameRepository
import io.critica.persistence.repository.PlayerRepository

class EventUseCase
//    private val gameRepository: GameRepository,
//    private val playerRepository: PlayerRepository,
//    private val eventRepository: EventRepository,

//    suspend fun day(request: DayStageRequest): DayStageResponse {
//        val game = gameRepository.get(request.gameId)
//        val dayNumber = getDayNumber(request.gameId) + 1
//        if (dayNumber % 2 != 0) {
//            throw BadRequestException("Cannot add day event to a night game")
//        }
//        if (game.dayEvents.count() >= 5) {
//            throw BadRequestException("Cannot add more than 5 day events to a game")
//        }
//
//        val candidates = Json.decodeFromString<List<Int>>(request.dayEvent.candidates!!)
//        for (candidate in candidates) {
//            if (playerDao.getPlayerById(candidate) == null) {
//                throw BadRequestException("Candidate not found")
//            }
//        }
//
//        val votes = Json.decodeFromString<Map<Int, Int>>(request.dayEvent.votes!!)
//        for (vote in votes.keys) {
//            if (!candidates.contains(vote)) {
//                throw BadRequestException("Vote not found")
//            }
//        }
//
//        gameRepository.addDayEvent(
//        game,
//        Json.decodeFromString(request.dayEvent.candidates!!),
//        Json.decodeFromString(request.dayEvent.votes!!))
//        return request.dayEvent.toResponse()
//    }
//
//    suspend fun night(request: NightStageRequest): NightStageResponse {
//        val game = gameRepository.get(request.gameId)
//        val dayNumber = GameController.getDayNumber(request.gameId) + 1
//        if (dayNumber % 2 == 0) {
//            throw BadRequestException("Cannot add night event to a day game")
//        }
//        if (game.status != GameStatus.STARTED) {
//            throw BadRequestException("Game is not started")
//        }
//        if (game.nightEvents.count() >= 5) {
//            throw BadRequestException("Cannot add more than 5 night events to a game")
//        }
//
//        val night = gameRepository.addNightEvent(game, request.mafiaShot, request.detectiveCheck, request.donCheck)
//        return night.toResponse()
//    }


//    companion object {
//        fun DayEvent.toResponse(
//        ): DayStageResponse = DayStageResponse(
//            day,
//            candidates.map { it.toPlayerResponse() },
//            votes.map { it.key.toPlayerResponse() to it.value.toPlayerResponse() }.toMap()
//        )
//
//        fun NightEvent.toResponse(
//        ): NightStageResponse = NightStageResponse(
//                dayNumber,
//                mafiaShot.toPlayerResponse(),
//                detectiveCheck.toPlayerResponse(),
//                donCheck.toPlayerResponse()
//            )
//    }
