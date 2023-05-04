package net.critika.persistence.exception

sealed class PlayerException(message: String) : Exception(message) {
    class NotFound(message: String) : PlayerException(message)
    class AlreadyInGame(message: String) : PlayerException(message)
}
