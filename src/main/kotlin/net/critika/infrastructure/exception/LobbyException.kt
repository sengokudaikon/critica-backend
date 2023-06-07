package net.critika.infrastructure.exception

import io.ktor.server.plugins.*

sealed class LobbyException(message: String) : Exception(message) {
    class NotFound(message: String) : LobbyException(message)
    class Full(message: String) : BadRequestException(message)

    class Expired(message: String) : BadRequestException(message)

    class NotInLobby(message: String) : BadRequestException(message)

    class AlreadyInLobby(message: String) : BadRequestException(message)

    class AlreadyCreated(message: String) : BadRequestException(message)
}
