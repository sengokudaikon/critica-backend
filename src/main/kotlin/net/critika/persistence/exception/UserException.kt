package net.critika.persistence.exception

sealed class UserException(message: String) : Exception(message) {
    class Invalid(message: String) : UserException(message)
    class NotFound(message: String) : UserException(message)
    class AlreadyExists(message: String) : UserException(message)
    class Unauthorized(message: String) : UserException(message)
}
