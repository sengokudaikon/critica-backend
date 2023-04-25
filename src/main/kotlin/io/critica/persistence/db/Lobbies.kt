package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object Lobbies: IdTable<UUID>(name = "lobbies") {
    override val id = uuid("id").entityId()
    val creator = reference("creator", Users)
    val date = varchar("date", 255)
    val name = varchar("name", 255)
}
