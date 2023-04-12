package io.critica.persistence

//import io.critica.application.lobby.GetLobby
import io.critica.application.lobby.request.GetLobby
import io.critica.domain.Game
import io.critica.domain.GameStatus
import io.critica.domain.Player
import io.critica.domain.events.DayEvent
import io.critica.domain.events.NightEvent
import io.critica.persistence.exception.GameException
import io.critica.persistence.repository.*

sealed class GameProcess (
    val playerRepository: PlayerRepository,
    val lobbyRepository: LobbyRepository,
    val gameRepository: GameRepository,
    val userRepository: UserRepository,
    val eventRepository: EventRepository
) {
    class Start(
        playerRepository: PlayerRepository,
        lobbyRepository: LobbyRepository,
        gameRepository: GameRepository,
        userRepository: UserRepository,
        eventRepository: EventRepository,
    ) : GameProcess(playerRepository, lobbyRepository, gameRepository, userRepository, eventRepository) {
        suspend fun addPlayerToLobby(getLobby: GetLobby, playerName: String): Player {
            val lobby = lobbyRepository.get(getLobby)
            val user = userRepository.findByUsername(playerName)

            return playerRepository.add(lobby.id.value, user)
        }

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
//        playerRepository: PlayerRepository,
//        lobbyRepository: LobbyRepository,
//        gameRepository: GameRepository,
//        userRepository: UserRepository,
//        eventRepository: EventRepository
//    ) : GameProcess(playerRepository, lobbyRepository, gameRepository, userRepository, eventRepository) {
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
//        playerRepository: PlayerRepository,
//        lobbyRepository: LobbyRepository,
//        gameRepository: GameRepository,
//        userRepository: UserRepository,
//        eventRepository: EventRepository
//    ) : GameProcess(playerRepository, lobbyRepository, gameRepository, userRepository, eventRepository) {
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