package net.critika.persistence.club.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.club.model.GameStatus
import net.critika.persistence.user.entity.Users
import org.jetbrains.exposed.sql.javatime.datetime

object Games : KotlinxUUIDTable("games") {
    val hostId = reference("host_id", Users).nullable()
    val lobbyId = reference("lobby_id", Lobbies)
    val tournamentId = reference("tournament_id", Tournaments).nullable()
    val date = datetime("date")
    val status = enumeration("status", GameStatus::class)
    val winner = varchar("winner", 255).nullable()
}
