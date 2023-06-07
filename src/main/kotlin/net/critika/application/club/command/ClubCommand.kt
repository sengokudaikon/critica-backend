package net.critika.application.club.command

import kotlinx.uuid.UUID
import net.critika.domain.club.model.Club

interface ClubCommand {
    data class Create(
        val name: String,
        val description: String,
        val country: String,
        val city: String,
        val address: String,
        val creatorId: UUID,
        val ruleSet: String,
        val logo: String,
    ): ClubCommand

    data class Update(
    val id: UUID,
    val name: String?,
    val description: String?,
    val country: String?,
    val city: String?,
    val address: String?,
    val ruleSet: String?,
    val logo: String?,
    ) : ClubCommand

    data class Delete(
        val id: UUID,
    ) : ClubCommand

    data class Save(
        val club: Club,
    ) : ClubCommand
}