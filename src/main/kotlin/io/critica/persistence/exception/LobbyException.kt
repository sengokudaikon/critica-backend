package io.critica.persistence.exception

import io.ktor.server.plugins.*

sealed class LobbyException(message: String): Exception(message) {
    class Full(message: String): BadRequestException(message)

    class Expired(message: String): BadRequestException(message)

    class NotInLobby(message: String): BadRequestException(message)
}