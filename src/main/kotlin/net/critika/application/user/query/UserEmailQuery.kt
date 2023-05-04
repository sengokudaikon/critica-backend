package net.critika.application.user.query

import net.critika.infrastructure.validation.constraints.ValidEmail

data class UserEmailQuery(
    @ValidEmail val email: String
)