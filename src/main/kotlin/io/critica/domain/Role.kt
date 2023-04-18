package io.critica.domain

enum class Role {
    CITIZEN,
    MAFIOSO,
    DON,
    DETECTIVE;

    fun toTeam(): String {
        return when (this) {
            CITIZEN, DETECTIVE -> "city"
            MAFIOSO, DON -> "mafia"
        }
    }

    fun toRole(string: String): Role {
        return when (string) {
            "citizen" -> CITIZEN
            "mafia" -> MAFIOSO
            else -> {
                throw IllegalArgumentException("Invalid role")}
        }
    }
}
