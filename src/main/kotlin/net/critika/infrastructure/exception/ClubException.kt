package net.critika.infrastructure.exception

import io.ktor.server.plugins.*

sealed class ClubException(message: String) : Exception(message) {
    class NotFound(message: String) : ClubException(message)
    class Full(message: String) : BadRequestException(message)

    class Expired(message: String) : BadRequestException(message)

    class NotInClub(message: String) : BadRequestException(message)

    class AlreadyInClub(message: String) : BadRequestException(message)

    class AlreadyCreated(message: String) : BadRequestException(message)
}