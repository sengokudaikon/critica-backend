package io.critica.persistence

//import io.critica.application.lobby.GetLobby
import io.critica.application.game.CreateGameRequest
import io.critica.application.lobby.request.GetLobby
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Player
import io.critica.persistence.exception.GameException
import io.critica.persistence.exception.LobbyException
import io.critica.persistence.repository.*
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.ktor.ext.inject

sealed class GameProcessor (
    game: Game? = null
): KoinComponent {
    val playerRepository: PlayerRepository by inject()
    val lobbyRepository: LobbyRepository by inject()
    val gameRepository: GameRepository by inject()
    val userRepository: UserRepository by inject()
    val eventRepository: EventRepository by inject()

    class Preparation: GameProcessor() {
//        suspend fun addPlayerToLobby(id: Int, playerName: String): Player {
//            val lobby = lobbyRepository.get(GetLobby(id))
//            val user = userRepository.findByUsername(playerName)
//
//            val result = playerRepository.add(lobby.id.value, user)
//
//            if (!lobby.players.contains(result)) throw LobbyException.AlreadyCreated("Player not in lobby")
//        }

//        suspend fun addPlayerToLobbyById(id: Int, playerId: Int): Player {
//            val lobby = lobbyRepository.get(GetLobby(id))
//            val user = userRepository.findById(playerId)
//
//            val result = playerRepository.add(lobby.id.value, user)
//            if (!lobby.players.contains(result)) throw LobbyException.AlreadyCreated("Player not in lobby")
//        }

//        suspend fun removePlayerFromLobby(id: Int, playerName: String): Player {
//            val lobby = lobbyRepository.get(GetLobby(id))
//            val user = userRepository.findByUsername(playerName)
//
//            return lobby.players.remove(lobby.id.value, user)
//        }

        suspend fun addGameToLobby(id: Int, time: LocalTime): Game {
            val lobby = lobbyRepository.get(GetLobby(id))
            val date = DateTime(lobby.date).withTime(time)
            val game = gameRepository.create(CreateGameRequest(date))

            if (lobby.games.contains(game)) throw LobbyException.AlreadyCreated("Game is already in lobby")
            if (game.status != GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
            if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
            if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
            if (!game.players.empty()) throw GameException.TooManyPlayers("There shouldn't be any players in game")

            return game
        }

        suspend fun removeGameFromLobby(id: Int, gameId: Int) {
            val lobby = lobbyRepository.get(GetLobby(id))
            val game = gameRepository.get(gameId)

            if (!lobby.games.contains(game)) throw LobbyException.NotFound("Game is not in lobby")
            if (game.status != GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
            if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
            if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
            if (!game.players.empty()) throw GameException.TooManyPlayers("There are players in the game")

            lobby.games.minus(game)
        }
    }

    class Start(
        game: Game
    ) : GameProcessor(game) {
        suspend fun addPlayerToGame(getLobby: GetLobby, playerId: Int, gameId: Int): Player {
            val lobby = lobbyRepository.get(getLobby)
            val game = gameRepository.get(gameId)

            if (!lobby.games.contains(game)) throw GameException.NotInLobby("Game is not in lobby")
            if (game.status == GameStatus.WAITING) throw GameException.NotWaiting("Game is not in status 'waiting'")
            if (game.status == GameStatus.FINISHED) throw GameException.AlreadyFinished("Game is in status 'finished'")
            if (game.status == GameStatus.STARTED) throw GameException.AlreadyStarted("Game is in status 'started'")
            if (game.players.count().toInt() == 10) throw GameException.TooManyPlayers("Too many players in game")

            val player = playerRepository.getPlayerByPlayerIdAndGameId(playerId, gameId)
            return playerRepository.addToGame(player!!, gameId)
        }
    }

//    class Night(
//        game: Game
//    ) : GameProcess(game: Game) {
//        suspend fun addNightEvent(game: Game): NightEvent {
//            return eventRepository.addNightEvent(game)
//        }
//
//        suspend fun addShot(game: Game, shot: Int): NightEvent {
//            return eventRepository.addShot(game, shot)
//        }
//
//        suspend fun addCheck(game: Game, check: Int): NightEvent {
//            return eventRepository.addCheck(game, check)
//        }
//
//        suspend fun addDonCheck(game: Game, donCheck: Int): NightEvent {
//            return eventRepository.addDonCheck(game, donCheck)
//        }
//    }
//
//    class Day(
//        game: Game
//    ) : GameProcess(game: Game) {
//        suspend fun startDay(game: Game, day: Int): DayEvent {
//            return eventRepository.startDay(game, day)
//        }
//
//        suspend fun addCandidate(game: Game, candidateId: Int): DayEvent {
//
//            return eventRepository.addCandidate(game, candidateId)
//        }
//
//        suspend fun addVote(game: Game, voter: Int, target: Int): DayEvent {
//            return eventRepository.addVote(game, voter, target)
//        }
//    }
}