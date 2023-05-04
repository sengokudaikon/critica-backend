package net.critika.persistence.exception

sealed class UserException(message: String): Exception(message) {
    class NotFound(message: String) : UserException(message)
    class AlreadyExists(message: String) : UserException(message)
}
