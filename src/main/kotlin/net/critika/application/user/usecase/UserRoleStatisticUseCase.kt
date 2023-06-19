package net.critika.application.user.usecase

import kotlinx.uuid.UUID
import net.critika.application.user.command.UserRoleStatisticsCommand
import net.critika.domain.user.model.RoleStatistic
import net.critika.domain.user.repository.UserRoleStatisticRepositoryPort
import net.critika.ports.user.UserRoleStatisticPort

class UserRoleStatisticUseCase(
    private val userRoleStatisticRepository: UserRoleStatisticRepositoryPort,
) : UserRoleStatisticPort {
    override suspend fun create(command: UserRoleStatisticsCommand): RoleStatistic {
        return userRoleStatisticRepository.create(command)
    }

    override suspend fun list(id: UUID): List<RoleStatistic> {
        return userRoleStatisticRepository.findByUserRating(id)
    }

    override suspend fun update(command: UserRoleStatisticsCommand): RoleStatistic {
        return userRoleStatisticRepository.update(command)
    }

    override suspend fun delete(id: UUID) {
        return userRoleStatisticRepository.delete(id)
    }

    override suspend fun get(id: UUID): RoleStatistic {
        return userRoleStatisticRepository.get(id)
    }

    override suspend fun list(): List<RoleStatistic> {
        return userRoleStatisticRepository.list()
    }
    suspend fun findByUserRatingId(id: UUID): List<RoleStatistic> {
        return userRoleStatisticRepository.findByUserRating(id)
    }
}
