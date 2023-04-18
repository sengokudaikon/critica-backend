package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object Lobbies: IntIdTable() {
    val creator = reference("creator", Users)
    val date = varchar("date", 255)
    val name = varchar("name", 255)
}
