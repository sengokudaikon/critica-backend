package net.critika.domain

enum class PlayerRole {
    CITIZEN,
    MAFIOSO,
    DON,
    DETECTIVE,
    ;

    fun toTeam(): PlayerRole {
        return when (this) {
            CITIZEN, DETECTIVE -> CITIZEN
            MAFIOSO, DON -> MAFIOSO
        }
    }

    fun opw(): PlayerRole {
        return when (this) {
            CITIZEN, DETECTIVE -> MAFIOSO
            MAFIOSO, DON -> CITIZEN
        }
    }
}
