package net.critika.application.club.command

import kotlinx.uuid.UUID

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
    ) : ClubCommand

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
}
