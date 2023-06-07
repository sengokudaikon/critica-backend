package net.critika.application.stage.usecase

import kotlinx.uuid.UUID
import net.critika.application.game.command.GameCommand
import net.critika.application.game.response.GameResponse
import net.critika.application.player.command.PlayerCommand
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.GameStatus
import net.critika.domain.gameprocess.model.DayEvent
import net.critika.domain.gameprocess.model.DayStage
import net.critika.domain.gameprocess.model.NightEvent
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.gameprocess.model.PlayerStatus
import net.critika.domain.gameprocess.repository.EventRepositoryPort
import net.critika.infrastructure.exception.PlayerException
import net.critika.infrastructure.exception.StageException
import net.critika.persistence.club.repository.GameRepository
import net.critika.persistence.club.repository.PlayerRepository
import net.critika.persistence.gameprocess.entity.DayEvents
import net.critika.persistence.gameprocess.entity.NightEvents
import net.critika.persistence.gameprocess.entity.Players
import net.critika.ports.stage.StagePort
import org.jetbrains.exposed.sql.and

@Suppress("TooManyFunctions")
class StageUseCase(
    private val eventRepository: EventRepositoryPort,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository,
) : StagePort {
    override suspend fun startDay(gameId: UUID, day: Int): GameResponse {
        val game = gameRepository.get(gameId)
        return eventRepository.startDay(game, day)
    }

    override suspend fun finishDay(day: DayEvent): GameResponse {
        val candidates = day.candidates
        val votes = day.votes
        val eliminatedPlayer = votes.groupBy { it.target }.maxBy { it.value.size }.key
        val game = gameRepository.get(day.game.value)
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

    override suspend fun startNight(gameId: UUID, night: Int): GameResponse {
        val game = gameRepository.get(gameId)
        return eventRepository.startNight(game, night)
    }

    override suspend fun finishNight(night: NightEvent): GameResponse {
        val mafiaShot = night.mafiaShot
        val detectiveCheck = night.detectiveCheck
        val donCheck = night.donCheck
        val eliminatedPlayer = mafiaShot?.let { Player[it] }
        val game = gameRepository.get(night.game.value)
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

    override suspend fun firstShot(stageId: UUID, shot: Int, bestMove: List<Int>): GameResponse {
        val night = eventRepository.findStage(stageId)
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        val game = gameRepository.get(night.game.value)
        val shotPlayer = Player.find { Players.seat eq shot }.first()
        return if (bestMove.isNotEmpty()) {
            val bestMovePlayers = bestMove.map { Player.find { Players.seat eq it }.first() }
            val mafia = bestMovePlayers
                .filter { it.role == PlayerRole.MAFIA.toString() || it.role == PlayerRole.DON.toString() }
            if (mafia.size == 2) {
                shotPlayer.bonusPoints += 25
            } else if (mafia.size == 3) {
                shotPlayer.bonusPoints += 40
            }

            night.toGameResponse(game, game.players.map { it.toResponse() }, bestMovePlayers.map { it.toResponse() })
        } else {
            night.toGameResponse(game, game.players.map { it.toResponse() })
        }
    }

    override suspend fun addCandidate(dayId: UUID, candidateSeat: Int): GameResponse {
        val day = eventRepository.findStage(dayId)
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        val game = gameRepository.get(day.game.value)
        return eventRepository
            .addCandidate(day, candidateSeat)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun removeCandidate(dayId: UUID, candidateSeat: Int): GameResponse {
        val day = eventRepository.findStage(dayId)
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        val game = gameRepository.get(day.game.value)
        return eventRepository
            .removeCandidate(day, candidateSeat)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun voteOnCandidate(dayId: UUID, candidateId: Int, voterId: Int): GameResponse {
        val day = eventRepository.findStage(dayId)
        if (day !is DayEvent) throw StageException.NotFound("Day not found")
        val game = gameRepository.get(day.game.value)
        return eventRepository
            .addVote(day, candidateId, voterId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun setShot(nightId: UUID, shotId: Int): GameResponse {
        val night = eventRepository.findStage(nightId)
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        val game = gameRepository.get(night.game.value)
        return eventRepository
            .addShot(night, shotId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun setCheck(nightId: UUID, checkedId: Int): GameResponse {
        val night = eventRepository.findStage(nightId)
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        val game = gameRepository.get(night.game.value)
        return eventRepository
            .addCheck(night, checkedId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun setDonCheck(nightId: UUID, donCheckId: Int): GameResponse {
        val night = eventRepository.findStage(nightId)
        if (night !is NightEvent) throw StageException.NotFound("Night not found")
        val game = gameRepository.get(night.game.value)
        return eventRepository
            .addDonCheck(night, donCheckId)
            .toGameResponse(game, game.players.map { it.toResponse() })
    }

    override suspend fun finishStage(stageId: UUID): GameResponse {
        return when (val stage = eventRepository.findStage(stageId)) {
            is DayEvent -> finishDay(stage)
            is NightEvent -> {
                finishNight(stage)
            }
            else -> throw StageException.NotFound("Stage not found")
        }
    }

    override suspend fun nextStage(stageId: UUID): GameResponse {
        return when (val stage = eventRepository.findStage(stageId)) {
            is DayEvent -> selectDayStage(stage)
            is NightEvent -> eventRepository.startDay(gameRepository.get(stage.game.value), stage.night + 1)
            else -> throw StageException.NotFound("Stage not found")
        }
    }

    private suspend fun votingPhase(day: DayEvent): GameResponse {
        val game = gameRepository.get(day.game.value)
        if (day.candidates.count().toInt() == 1) {
            day.stage = DayStage.END
            eventRepository.save(day)
            if (day.day > 0) {
                game.playersEliminated.plus(eliminatePlayer(day.candidates.first().player.id.value))
            }
            return eventRepository.startNight(gameRepository.get(day.game.value), day.day + 1)
        }

        val voteGroups = day.votes.groupBy { it.target }
        val maxVotes = voteGroups.values.maxOf { it.size }
        val maxVotedTargets = voteGroups.filterValues { it.size == maxVotes }.keys

        return if (maxVotedTargets.isEmpty()) {
            day.stage = DayStage.END
            eventRepository.save(day)
            eventRepository.startNight(gameRepository.get(day.game.value), day.day + 1)
        } else if (maxVotedTargets.size == 1) {
            day.stage = DayStage.END
            eventRepository.save(day)
            game.playersEliminated.plus(eliminatePlayer(maxVotedTargets.first().id.value))
            eventRepository.startNight(gameRepository.get(day.game.value), day.day + 1)
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
        return playerRepository.update(PlayerCommand.Save(player))
    }

    override suspend fun selectDayStage(day: DayEvent): GameResponse {
        val game = gameRepository.get(day.game.value)
        return when (day.stage) {
            DayStage.DISCUSS -> {
                day.stage = DayStage.VOTE
                eventRepository.save(day)
                day.toGameResponse(game, game.players.map { it.toResponse() })
            }
            DayStage.VOTE, DayStage.REVOTE -> votingPhase(day)
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
                eventRepository.startNight(gameRepository.get(day.game.value), day.day)
            }
        }
    }

    override suspend fun previousStage(stageId: UUID): GameResponse {
        return when (val stage = eventRepository.findStage(stageId)) {
            is DayEvent -> {
                val game = gameRepository.get(stage.game.value)
                NightEvent.find { NightEvents.game eq game.id and (NightEvents.night eq stage.day - 1) }.last()
                    .toGameResponse(
                        game,
                        game.players.map { it.toResponse() },
                    )
            }
            is NightEvent -> {
                val game = gameRepository.get(stage.game.value)
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

    override suspend fun addFoul(gameId: UUID, seat: Int): Player {
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)
        player.foulPoints += 1
        if (player.foulPoints >= 3) {
            player.status = PlayerStatus.DEAD.toString()
            player.bonusPoints -= 40
        }
        return playerRepository.update(PlayerCommand.Save(player))
    }

    override suspend fun addBonus(gameId: UUID, seat: Int): Player {
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)
        player.bonusPoints += 10
        return playerRepository.update(PlayerCommand.Save(player))
    }

    override suspend fun opw(gameId: UUID, seat: Int): Game {
        val game = gameRepository.get(gameId)
        val player = playerRepository.getPlayerInGameBySeat(gameId, seat)

        val opw = player.role?.let { PlayerRole.valueOf(it) }?.opw()
        return gameRepository.update(GameCommand.Update(game, game.host, GameStatus.FINISHED, winner = opw))
    }
}
