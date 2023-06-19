package net.critika.application.user.command

import kotlinx.uuid.UUID
import net.critika.domain.user.model.RoleStatistic

interface UserRoleStatisticsCommand {
    class Create(
        val userId: UUID,
        val ratingId: UUID,
        val role: String,
    ) : UserRoleStatisticsCommand

    class Update(val roleStatistic: RoleStatistic) : UserRoleStatisticsCommand
}
