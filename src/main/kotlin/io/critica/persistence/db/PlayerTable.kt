package io.critica.persistence.db

import io.critica.domain.Player
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object PlayerTable : IntIdTable() {
    val userId = reference("userId", UserTable)
    val gameId = reference("gameId", GameTable)
    val name = varchar("name", 255)
    val status = varchar("status", 255)
    val role = varchar("role", 255)

    fun toPlayer(it: ResultRow): Player {
        val id = it[PlayerTable.id].value
        val userId = it[PlayerTable.userId].value
        val name = it[PlayerTable.name]
        val gameId = it[PlayerTable.gameId].value
        val role = it[PlayerTable.role]
        val status = it[PlayerTable.status]

        return Player(id, userId, gameId, name, role, status)
    }
}