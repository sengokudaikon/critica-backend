package net.critika.infrastructure.authentication

import io.ktor.server.auth.*

data class FirebasePrincipal(val uid: String) : Principal
