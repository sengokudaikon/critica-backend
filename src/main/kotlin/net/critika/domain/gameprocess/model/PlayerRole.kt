package net.critika.domain.gameprocess.model

enum class PlayerRole {
    CITIZEN,
    MAFIA,
    DON,
    DETECTIVE,
    ;

    fun toTeam(): PlayerRole {
        return when (this) {
            CITIZEN, DETECTIVE -> CITIZEN
            MAFIA, DON -> MAFIA
        }
    }

    fun opw(): PlayerRole {
        return when (this) {
            CITIZEN, DETECTIVE -> MAFIA
            MAFIA, DON -> CITIZEN
        }
    }
}
