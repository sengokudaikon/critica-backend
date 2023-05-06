package net.critika.usecase.stage

import net.critika.application.game.response.GameResponse
import net.critika.domain.Game
import net.critika.domain.GameStatus
import net.critika.domain.Player
import net.critika.domain.PlayerRole
import net.critika.domain.PlayerStatus
import net.critika.domain.events.model.DayEvent
import net.critika.domain.events.model.DayStage
import net.critika.domain.events.model.NightEvent
import net.critika.domain.events.repository.EventRepository
import net.critika.persistence.db.DayEvents
import net.critika.persistence.db.NightEvents
import net.critika.persistence.db.Players
import net.critika.persistence.exception.PlayerException
import net.critika.persistence.exception.StageException
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.PlayerRepository
import org.jetbrains.exposed.sql.and
import java.util.*

@Suppress("TooManyFunctions")
class StageUseCase(
    private val eventRepository: EventRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository,
) {
    suspend fun startDay(gameId: UUID, day: Int): GameResponse {
        val game = gameRepository.get(gameId)
        return eventRepository.startDay(game, day)
    }

    suspend fun finishDay(game: Game, day: DayEvent): GameResponse {
        val candidates = day.candidates
        val votes = day.votes
        val eliminatedPlayer = votes.groupBy { it.target }.maxBy { it.value.size }.key
        val gameResponse = GameResponse(
            id = game.toString(),
            date = game.date.toString(),
            players = game.players.map { it.toResponse() },
            host = game.host?.toPlayer()?.toResponse(),
            currentStage = day.toResponse(),
            nominates = candidates.map { it.toResponse() },
            votes = votes.map { it.toResponse() },
            playersEliminated = game.playersEliminated.plus(eliminatedPlayer).map { it.toResponse() },
        )
        eventRepository.save(day)
        return gameResponse
    }

    suspend fun startNight(gameId: UUID, night: Int): GameResponse {
        val game = gameRepository.get(gameId)
        return eventRepository.startNight(game, night)
    }

    suspend fun finishNight(game: Game, night: NightEvent): GameResponse {
        val mafiaShot = night.mafiaShot
        val detectiveCheck = night.detectiveCheck
        val donCheck = night.donCheck
        val eliminatedPlayer = mafiaShot?.let { Player[it] }
        val gameResponse = GameResponse(
            id = game.toString(),
            date = game.date.toString(),
            players = game.players.map { it.toResponse() },
            host = game.host?.toPlayer()?.toResponse(),
            currentStage = night.toResponse(),
            mafiaShot = mafiaShot?.let { Player[it].toResponse() },
            detectiveCheck = detectiveCheck?.let { Player[it].toResponse() },
            donCheck = donCheck?.let { Player[it].toResponse() },
            playersEliminated = game.playersEliminated.plus(eliminatedPlayer)
                .map { it?.toResponse() ?: throw PlayerException.NotFound("Player not found") },
        )

        eventRepository.save(night)
        return gameResponse
    }

    suspend fun firstShot(gameId: UUID, shot: Int, bestMove: List<Int>): GameResponse {
        val game = gameRepository.get(gameId)
        val night = game.nightEvents.first()
        val event = eventRepository.addShot(night, shot)
        val shotPlayer = Player.find { Players.seat eq shot }.first()
        return if (bestMove.isNotEmpty()) {
            // if 2 of the max 3 elements contain player id that has role == mafia or don, then add bonus points
            val bestMovePlayers = bestMove.map { Player.find { Players.seat eq it }.first() }
            val mafia = bestMovePlayers
                .filter { it.role == PlayerRole.MAFIA.toString() || it.role == PlayerRole.DON.toString() }
            if (mafia.size == 2) {
                shotPlayer.bonusPoints += 25
            } else if (mafia.size == 3) {
                shotPlayer.bonusPoints += 40
            }
            eliminatePlayer(shotPlayer.id.value)

            event.toGameResponse(
                game,
                game.players.map { it.toResponse() },
                bestMove = bestMovePlayers.map { it.toResponse() },
            )
        } else {
            event.toGameResponse(game, game.players.map { it.toResponse() })
        }
    }

    suspend fun addCandidate(gameId: UUID, dayId: UUID, candidateSeat: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val day = eventRepository.findStage(dayId)
        if (game.dayEvents.none { it.id.value == dayId }) throw StageException.NotFound("Day not found")
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        return eventRepository
            .addCandidate(day, candidateSeat)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun removeCandidate(gameId: UUID, dayId: UUID, candidateSeat: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val day = eventRepository.findStage(dayId)
        if (game.dayEvents.none { it.id.value == dayId }) throw StageException.NotFound("Day not found")
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        return eventRepository
            .removeCandidate(day, candidateSeat)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun voteOnCandidate(gameId: UUID, dayId: UUID, candidateId: Int, voterId: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val day = eventRepository.findStage(dayId)
        if (game.dayEvents.none { it.id.value == dayId }) throw StageException.NotFound("Day not found")
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        return eventRepository
            .addVote(day, candidateId, voterId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun setShot(gameId: UUID, nightId: UUID, shotId: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val night = eventRepository.findStage(nightId)
        if (game.nightEvents.none { it.id.value == nightId }) throw StageException.NotFound("Night not found")
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        return eventRepository
            .addShot(night, shotId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun setCheck(gameId: UUID, nightId: UUID, checkedId: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val night = eventRepository.findStage(nightId)
        if (game.nightEvents.none { it.id.value == nightId }) throw StageException.NotFound("Night not found")
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        return eventRepository
            .addCheck(night, checkedId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun setDonCheck(gameId: UUID, nightId: UUID, donCheckId: Int): GameResponse {
        val game = gameRepository.get(gameId)
        val night = eventRepository.findStage(nightId)
        if (game.nightEvents.none { it.id.value == nightId }) throw StageException.NotFound("Night not found")
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        return eventRepository
            .addDonCheck(night, donCheckId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    suspend fun finishStage(gameId: UUID, stageId: UUID): GameResponse {
        val game = gameRepository.get(gameId)
        if (
            game.nightEvents.none { it.id.value == stageId } ||
            game.dayEvents.none { it.id.value == stageId }
        ) {
            throw StageException.NotFound("Day not found")
        }
        val stage = eventRepository.findStage(stageId)
        return when (stage) {
            is DayEvent -> finishDay(game, stage)
            is NightEvent -> finishNight(game, stage)
            else -> throw StageException.NotFound("Stage not found")
        }
    }

    suspend fun nextStage(gameId: UUID, stageId: UUID): GameResponse {
        val game = gameRepository.get(gameId)
        if (
            game.dayEvents.none { it.id.value == stageId } &&
            game.nightEvents.none { it.id.value == stageId }
        ) {
            throw StageException.NotFound("Stage not found")
        }
        val stage = eventRepository.findStage(stageId)
        return when (stage) {
            is DayEvent -> selectDayStage(game, stage)
            is NightEvent -> eventRepository.startDay(game, stage.night + 1)
            else -> throw StageException.NotFound("Stage not found")
        }
    }

    private suspend fun votingPhase(game: Game, day: DayEvent): GameResponse {
        if (day.candidates.count().toInt() == 1) {
            day.stage = DayStage.END
            eventRepository.save(day)
            if (day.day > 0) {
                game.playersEliminated.plus(eliminatePlayer(day.candidates.first().player.id.value))
            }
            return eventRepository.startNight(game, day.day + 1)
        }

        val voteGroups = day.votes.groupBy { it.target }
        val maxVotes = voteGroups.values.maxOf { it.size }
        val maxVotedTargets = voteGroups.filterValues { it.size == maxVotes }.keys

        return if (maxVotedTargets.isEmpty()) {
            day.stage = DayStage.END
            eventRepository.save(day)
            eventRepository.startNight(game, day.day + 1)
        } else if (maxVotedTargets.size == 1) {
            day.stage = DayStage.END
            eventRepository.save(day)
            game.playersEliminated.plus(eliminatePlayer(maxVotedTargets.first().id.value))
            eventRepository.startNight(game, day.day + 1)
        } else {
            eventRepository.save(day)
            val newStage = if (day.stage == DayStage.VOTE) DayStage.REDISCUSS else DayStage.BOTH
            val newDay = eventRepository.createNewDayEvent(game, day.day, newStage, maxVotedTargets)
            newDay.toGameResponse(game, game.players.map { it.toResponse() })
        }
    }

    private suspend fun eliminatePlayer(playerId: UUID): Player {
        val player = playerRepository.get(playerId)
        player.status = PlayerStatus.DEAD.toString()
        return playerRepository.save(player)
    }

    suspend fun selectDayStage(game: Game, day: DayEvent): GameResponse {
        return when (day.stage) {
            DayStage.DISCUSS -> {
                day.stage = DayStage.VOTE
                eventRepository.save(day)
                day.toGameResponse(game, game.players.map { it.toResponse() })
            }
            DayStage.VOTE, DayStage.REVOTE -> votingPhase(game, day)
            DayStage.REDISCUSS -> {
                day.stage = DayStage.REVOTE
                eventRepository.save(day)
                day.toGameResponse(game, game.players.map { it.toResponse() })
            }
            DayStage.BOTH -> {
                val totalVotes = day.votes.count()
                val nonEliminatedPlayers = game.players.count() - game.playersEliminated.count()

                if (totalVotes > nonEliminatedPlayers / 2) {
                    day.stage = DayStage.END
                    eventRepository.save(day)
                    game.playersEliminated.plus(
                        day.candidates.map {
                            eliminatePlayer(it.id.value)
                        },
                    )
                } else {
                    day.stage = DayStage.END
                    eventRepository.save(day)
                }
                day.toGameResponse(game, game.players.map { it.toResponse() })
            }
            DayStage.END -> {
                eventRepository.save(day)
                eventRepository.startNight(game, day.day)
            }
        }
    }

    suspend fun previousStage(gameId: UUID, stageId: UUID): GameResponse {
        val game = gameRepository.get(gameId)
        if (
            game.dayEvents.none { it.id.value == stageId } &&
            game.nightEvents.none { it.id.value == stageId }
        ) {
            throw StageException.NotFound("Stage not found")
        }
        val stage = eventRepository.findStage(stageId)
        return when (stage) {
            is DayEvent -> {
                NightEvent.find { NightEvents.game eq game.id and (NightEvents.night eq stage.day - 1) }.last()
                    .toGameResponse(
                        game,
                        game.players.map { it.toResponse() },
                    )
            }
            is NightEvent -> {
                DayEvent.find { DayEvents.game eq game.id and (DayEvents.day eq stage.night) }.last()
                    .toGameResponse(
                        game,
                        game.players.map { it.toResponse() },
                    )
            }

            else -> {
                throw StageException.NotFound("Stage not found")
            }
        }
    }

    suspend fun addFoul(gameId: UUID, seat: Int): Player {
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)
        player.foulPoints += 1
        if (player.foulPoints >= 3) {
            player.status = PlayerStatus.DEAD.toString()
            player.bonusPoints -= 40
        }
        return playerRepository.save(player)
    }

    suspend fun addBonus(gameId: UUID, seat: Int): Player {
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)
        player.bonusPoints += 10
        return playerRepository.save(player)
    }

    suspend fun opw(gameId: UUID, seat: Int): Game {
        val game = gameRepository.get(gameId)
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)

        val opw = player.role?.let { PlayerRole.valueOf(it) }?.opw()
        return gameRepository.update(game, GameStatus.FINISHED, opw)
    }
}
