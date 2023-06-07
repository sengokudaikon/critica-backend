package net.critika.infrastructure.authentication

import net.critika.persistence.user.repository.UserRepository
import org.koin.core.annotation.Single

// Manages access to user repository from upper level without exposing usecase layer
@Single
class AuthPrincipality(
    val userRepository: UserRepository,
)
