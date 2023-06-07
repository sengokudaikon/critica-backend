package net.critika.application.game.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.server.plugins.*
import kotlinx.uuid.UUID
import net.critika.application.game.command.GameCommand
import net.critika.application.game.response.GameResponse
import net.critika.application.user.usecase.UserStatisticsUseCase
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.GameStatus
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.gameprocess.model.PlayerStatus
import net.critika.domain.user.model.UserRole
import net.critika.domain.user.repository.UserRepositoryPort
import net.critika.infrastructure.exception.PlayerException
import net.critika.infrastructure.exception.UserException
import net.critika.persistence.club.repository.GameRepository
import net.critika.persistence.club.repository.LobbyRepository
import net.critika.persistence.club.repository.PlayerRepository
import net.critika.ports.game.GamePort
import org.koin.core.annotation.Single

private const val MAX_PLAYERS: Int = 10
@Single
class GameUseCase(
    private val repository: GameRepository,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository,
    private val userRepository: UserRepositoryPort,
    private val userStatisticsUseCase: UserStatisticsUseCase,
): GamePort {
    override suspend fun assignHost(gameId: UUID, hostId: UUID) {
        val game = repository.get(gameId)
        val host = userRepository.findById(hostId) ?: throw UserException.NotFound("User not found")
        require(host.role == UserRole.HOST) { "Player is not host" }
        game.host = host
        repository.update(GameCommand.Update(game, status = GameStatus.WAITING))
    }

    override suspend fun get(id: UUID): GameResponse {
        val game = repository.get(id)
        val players = game.players.map { it.toResponse() }
        if (game.status != GameStatus.STARTED) {
            return GameResponse(
                id = game.id.value.toString(),
                date = game.date.toString(),
                host = game.host?.toPlayer()?.toResponse(),
                players,
                null,
            )
        }
        val currentStage = game.getCurrentStage()
        return currentStage.toGameResponse(game, players)
    }

    override suspend fun list(): List<GameResponse> {
        return repository.list().map { it.toResponse() }
    }

    override suspend fun start(gameId: UUID): Either<Exception, Game> {
        val game = repository.get(gameId)
        game.lobby.players.apply { minus(game.players) }
        return if (game.status != GameStatus.CREATED || game.status != GameStatus.WAITING) {
            BadRequestException("Game is already started").left()
        } else {
            repository.update(GameCommand.Update(game, status = GameStatus.STARTED)).right()
        }
    }

    override suspend fun addPlayerById(gameId: UUID, playerId: UUID): Either<Exception, Player> {
        return try {
            val game = repository.get(gameId)
            val lobby = lobbyRepository.getByGameId(gameId)

            require(!lobby.games.contains(game)) { "Game is not in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.status == GameStatus.FINISHED) { "Game is in status 'finished'" }
            require(game.status == GameStatus.STARTED) { "Game is in status 'started'" }
            require(game.players.count().toInt() == MAX_PLAYERS) { "Too many players in game" }

            val player = playerRepository.getPlayerByPlayerIdAndGameId(playerId, gameId)
            requireNotNull(player) { "Player not found" }
            val result = game.players.plus(player)

            require(!result.contains(player)) { "Player is already in game" }

            player.right()
        } catch (e: Exception) {
            e.left()
        }
    }

    override suspend fun addPlayerByName(gameId: UUID, playerName: String): Either<Exception, Player> {
        return try {
            val game = repository.get(gameId)
            val lobby = lobbyRepository.getByGameId(gameId)

            require(!lobby.games.contains(game)) { "Game is not in lobby" }
            require(game.status == GameStatus.WAITING) { "Game is not in status 'waiting'" }
            require(game.status == GameStatus.FINISHED) { "Game is in status 'finished'" }
            require(game.status == GameStatus.STARTED) { "Game is in status 'started'" }
            require(game.players.count().toInt() == MAX_PLAYERS) { "Too many players in game" }

            val player = playerRepository.getPlayerByPartialNameAndGameId(playerName, gameId)

            if (player != null) {
                game.players.plus(player)
                player.right()
            } else {
                val tempPlayer = playerRepository.createTemporaryPlayer(playerName, lobby.id.value, gameId)
                game.players.plus(tempPlayer)
                tempPlayer.right()
            }
        } catch (e: Exception) {
            return e.left()
        }
    }

    override suspend fun removePlayerById(gameId: UUID, playerId: UUID): Either<Exception, Any?> {
        val player = playerRepository.get(playerId)
        val game = repository.get(gameId)

        val result = game.players.minus(player)

        return if (result.contains(player)) {
            PlayerException.AlreadyInGame("Player is still in game").left()
        } else {
            null.right()
        }
    }

    @Suppress("NestedBlockDepth")
    override suspend fun finish(gameId: UUID, winningTeam: PlayerRole): Map<PlayerRole, MutableList<Player>> {
        val game = repository.get(gameId)
        repository.update(GameCommand.Update(game, status = GameStatus.FINISHED, winner = winningTeam))

        val winningPlayers = mutableListOf<Player>()

        game.players.forEach {
            if (it.user != null) {
                val userRating = userStatisticsUseCase.getUserRating(it.user!!.id.value) ?: throw IllegalStateException(
                    "User rating not found",
                )
                val roleStatistic = userRating.roleStatistics.find { it.role.toTeam() == winningTeam }
                    ?: throw IllegalStateException("Role statistic not found")

                if (it.bestMove > 0) {
                    userRating.bestMovePoints += it.bestMove
                }

                if (it.bonusPoints > 0) {
                    userRating.bonusPoints += it.bonusPoints
                }

                if (
                    (PlayerStatus.valueOf(it.status) == PlayerStatus.REMOVED) ||
                    (PlayerStatus.valueOf(it.status) == PlayerStatus.OPW)
                ) {
                    userRating.malusPoints += 50
                    userRating.totalPoints -= 50
                }

                if (it.role != null) {
                    if (PlayerRole.valueOf(it.role!!).toTeam() == winningTeam) {
                        userRating.totalPoints += 100
                        roleStatistic.gamesWon += 1
                        roleStatistic.gamesTotal += 1
                        roleStatistic.bonusPoints += it.bonusPoints
                        winningPlayers.add(it)
                    }
                }

                userStatisticsUseCase.updateUserRating(userRating)
                userStatisticsUseCase.updateRoleStatistic(roleStatistic)
            }
            game.lobby.players.plus(it)
            it.seat = 0
            it.game = null
            it.role = null
            it.status = PlayerStatus.WAITING.name
            it.bestMove = 0
            it.bonusPoints = 0
            it.foulPoints = 0
        }

        return mapOf(winningTeam to winningPlayers)
    }
}