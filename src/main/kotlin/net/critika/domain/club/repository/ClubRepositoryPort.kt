package net.critika.domain.club.repository

import kotlinx.uuid.UUID
import net.critika.application.club.command.ClubCommand
import net.critika.domain.club.model.Club
import net.critika.ports.CrudPort

interface ClubRepositoryPort : CrudPort<ClubCommand, Club> {
    suspend fun addUserToClub(userId: UUID, clubId: UUID): Boolean
    suspend fun removeUserFromClub(userId: UUID, clubId: UUID): Boolean
}
