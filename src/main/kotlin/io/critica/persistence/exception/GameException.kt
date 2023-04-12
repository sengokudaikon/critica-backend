package io.critica.persistence.exception

import io.ktor.server.plugins.*

sealed class GameException(message: String): Exception(message) {
    class NotFound(message: String): GameException(message)
    class Full(message: String): BadRequestException(message)
    class NotInLobby(message: String): BadRequestException(message)

    class NotEnoughPlayers(message: String): BadRequestException(message)

    class TooManyPlayers(message: String): BadRequestException(message)

    class AlreadyStarted(message: String): BadRequestException(message)

    class AlreadyFinished(message: String): BadRequestException(message)

    class NotWaiting(message: String): BadRequestException(message)
}