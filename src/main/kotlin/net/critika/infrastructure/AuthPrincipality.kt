package net.critika.infrastructure

import net.critika.persistence.repository.UserRepositoryImpl
import org.koin.core.annotation.Single

// Manages access to user repository from upper level without exposing usecase layer
@Single
class AuthPrincipality(
    val userRepository: UserRepositoryImpl,
)
