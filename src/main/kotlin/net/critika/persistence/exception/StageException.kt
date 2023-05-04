package net.critika.persistence.exception

import io.swagger.codegen.v3.service.exception.BadRequestException

sealed class StageException(message: String) : Exception(message) {
    class NotFound(message: String) : StageException(message)
    class Full(message: String) : BadRequestException(message)

    class Expired(message: String) : BadRequestException(message)

    class NotInStage(message: String) : BadRequestException(message)

    class AlreadyInStage(message: String) : BadRequestException(message)

    class AlreadyCreated(message: String) : BadRequestException(message)
}
