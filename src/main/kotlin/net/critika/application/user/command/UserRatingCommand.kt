package net.critika.application.user.command

import kotlinx.uuid.UUID
import net.critika.domain.user.model.UserRating

interface UserRatingCommand {
    class Create(val userId: UUID) : UserRatingCommand
    class Update(val userRating: UserRating) : UserRatingCommand
}
