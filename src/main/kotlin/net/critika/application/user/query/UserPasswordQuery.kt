package net.critika.application.user.query

import net.critika.infrastructure.validation.constraints.ValidPassword

data class UserPasswordQuery(
    @ValidPassword val password: String
)
