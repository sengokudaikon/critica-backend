package net.critika.ports.user

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRoleStatisticsCommand
import net.critika.domain.user.model.RoleStatistic
import net.critika.ports.CrudPort

interface UserRoleStatisticPort : CrudPort<UserRoleStatisticsCommand, RoleStatistic> {
    suspend fun list(id: UUID): List<RoleStatistic>
}
