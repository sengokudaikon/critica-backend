package net.critika.adapters

import io.ktor.server.application.*
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthorizationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Controller: KoinComponent {
    private val authorization: AuthorizationService by inject()
    protected suspend fun <T> authorize(
        call: ApplicationCall,
        requiredRoles: List<UserRole>,
        block: suspend () -> T,
    ) {
        return authorization.authorize(call, requiredRoles, block)
    }
}