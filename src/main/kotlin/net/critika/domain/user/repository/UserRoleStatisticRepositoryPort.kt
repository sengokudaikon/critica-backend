package net.critika.domain.user.repository

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRoleStatisticsCommand
import net.critika.domain.user.model.RoleStatistic
import net.critika.ports.CrudPort

interface UserRoleStatisticRepositoryPort : CrudPort<UserRoleStatisticsCommand, RoleStatistic> {
    suspend fun findByUserRating(id: UUID): List<RoleStatistic>
}
