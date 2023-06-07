package net.critika.persistence.club.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.javatime.datetime

object Lobbies : KotlinxUUIDTable(name = "lobbies") {
    val clubId = reference("club_id", Clubs)
    val creator = reference("creator", Users)
    val date = datetime("date")
}
