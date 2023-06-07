package net.critika.ports.user

import kotlinx.uuid.UUID
import net.critika.domain.gameprocess.model.PlayerRole
import net.critika.domain.user.model.RoleStatistic

interface UserRoleStatisticPort {
    suspend fun createRoleStatistic(userRatingId: UUID, role: PlayerRole): RoleStatistic
    suspend fun getRoleStatisticsByUserRatingId(userRatingId: UUID): List<RoleStatistic>
    suspend fun updateRoleStatistic(roleStatistic: RoleStatistic)
    suspend fun deleteRoleStatistic(id: UUID)
}