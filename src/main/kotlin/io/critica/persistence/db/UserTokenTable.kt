package io.critica.persistence.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTokenTable : IntIdTable() {
    val userId = reference("userId", UserTable)
    val token = varchar("token", length = 200)
    val expiryTime = long("expiryTime")

    init {
        index(true, userId, expiryTime)
    }
}
