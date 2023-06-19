package net.critika.application.tournament.command

import kotlinx.uuid.UUID

interface TournamentCommand {
    data class Create(
        val name: String,
        val description: String,
        val startDate: String,
        val location: String,
        val playerLimit: Int,
        val creatorId: UUID,
        val clubId: UUID?,
    ) : TournamentCommand

    data class Update(
        val id: UUID,
        val name: String?,
        val description: String?,
        val startDate: String?,
        val location: String?,
        val isFinished: Boolean?,
        val playerLimit: Int?,
        val clubId: UUID?,
    ) : TournamentCommand
}
