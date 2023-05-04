package net.critika.usecase.game

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.server.plugins.*
import net.critika.application.game.response.GameResponse
import net.critika.domain.Game
import net.critika.domain.GameStatus
import net.critika.domain.Player
import net.critika.domain.PlayerRole
import net.critika.domain.PlayerStatus
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRole
import net.critika.persistence.exception.PlayerException
import net.critika.persistence.repository.GameRepository
import net.critika.persistence.repository.LobbyRepository
import net.critika.persistence.repository.PlayerRepository
import net.critika.usecase.user.UserStatisticsUseCase
import org.koin.core.annotation.Single
import java.util.*

private const val MAX_PLAYERS: Int = 10

@Single
class GameUseCase(
    private val repository: GameRepository,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository,
    private val userStatisticsUseCase: UserStatisticsUseCase,
) {
    suspend fun assignHost(gameId: UUID, hostId: UUID) {
        val game = repository.get(gameId)
        val host = User[hostId]
        require(host.role == UserRole.ADMIN) { "Player is not host" }
        game.host = host
        repository.update(game, status = GameStatus.WAITING)
    }

    suspend fun get(gameId: UUID): GameResponse {
        val game = repository.get(gameId)
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

    suspend fun list(): List<Game> {
        return repository.getGames()
    }

    suspend fun start(gameId: UUID): Either<Exception, Game> {
        val game = repository.get(gameId)
        game.lobby.players.apply { minus(game.players) }
        return if (game.status != GameStatus.CREATED) {
            BadRequestException("Game is already started").left()
        } else {
            repository.update(game, status = GameStatus.STARTED).right()
        }
    }

    suspend fun addPlayerById(gameId: UUID, playerId: UUID): Either<Exception, Player> {
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

    suspend fun addPlayerByName(gameId: UUID, playerName: String): Either<Exception, Player> {
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

    suspend fun removePlayerById(gameId: UUID, playerId: UUID): Either<Exception, Any?> {
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
    suspend fun finish(gameId: UUID, winningTeam: PlayerRole): Map<PlayerRole, MutableList<Player>> {
        val game = repository.get(gameId)
        repository.update(game, GameStatus.FINISHED, winningTeam)

        val winningPlayers = mutableListOf<Player>()

        game.players.forEach {
            if (it.user != null) {
                val userRating = userStatisticsUseCase.getUserRating(it.user!!.id.value) ?: throw IllegalStateException(
                    "User rating not found",
                )
                val roleStatistic = userStatisticsUseCase.getRoleStatistic(it.user!!.id.value)
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
            it.user = null
            it.game = null
        }
        game.lobby.players.plus(game.players)

        return mapOf(winningTeam to winningPlayers)
    }
}
