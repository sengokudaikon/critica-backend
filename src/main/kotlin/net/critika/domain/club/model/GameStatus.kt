package net.critika.domain.club.model

enum class GameStatus {
    CREATED,
    WAITING,
    STARTED,
    FINISHED,
    ;

    companion object {
        fun fromString(status: String): GameStatus {
            // return value based on string matching value.name
            return values().first { it.name == status }
        }
    }
}
