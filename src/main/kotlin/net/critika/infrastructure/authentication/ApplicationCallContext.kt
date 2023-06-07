package net.critika.infrastructure.authentication

import io.ktor.server.application.*
import net.critika.domain.user.repository.UserRepositoryPort

data class ApplicationCallContext(val call: ApplicationCall, val userRepository: UserRepositoryPort)

