package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Lobbies : UUIDTable(name = "lobbies") {
    val creator = reference("creator", Users)
    val date = datetime("date")
}
