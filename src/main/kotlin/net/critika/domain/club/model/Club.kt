package net.critika.domain.club.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.application.club.response.ClubResponse
import net.critika.domain.user.model.User
import net.critika.persistence.club.entity.Clubs
import net.critika.persistence.club.entity.Lobbies
import org.jetbrains.exposed.dao.id.EntityID

class Club(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Club>(Clubs)

    var name by Clubs.name
    var creator by User referencedOn Clubs.creator
    val members by User optionalReferrersOn Clubs.members
    val lobbies by Lobby referrersOn Lobbies.clubId
    val games by Game referrersOn Lobbies.clubId
    val createdAt by Clubs.createdAt
    var updatedAt by Clubs.updatedAt
    var country by Clubs.country
    var city by Clubs.city
    var address by Clubs.address
    var logo by Clubs.logo
    var description by Clubs.description
    var ruleSet by Clubs.ruleSet

    fun toResponse(): ClubResponse {
        return ClubResponse(
            id = this.id.value.toString(),
            name = this.name,
            creator = this.creator.toResponse(),
            members = this.members.map { it.toResponse() },
            lobbies = this.lobbies.map { it.toResponse() },
            games = this.games.map { it.toResponse() },
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString(),
            country = this.country,
            logo = this.logo,
            description = this.description,
            ruleSet = this.ruleSet.name,
            city = this.city,
            address = this.address
        )
    }
}
