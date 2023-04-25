package io.critica.infrastructure

import io.critica.persistence.repository.UserRepository

//Manages access to user repository from upper level without exposing usecase layer
class AuthPrincipality (
    val userRepository: UserRepository
)
