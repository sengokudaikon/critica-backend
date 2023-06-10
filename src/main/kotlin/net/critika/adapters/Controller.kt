package net.critika.adapters

import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.uuid.UUID
import net.critika.domain.user.model.UserRole
import net.critika.infrastructure.AuthorizationService
import net.critika.infrastructure.authentication.FirebasePrincipal
import net.critika.infrastructure.exception.UserException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Controller : KoinComponent {
    private val authorization: AuthorizationService by inject()
    protected suspend fun <T> authorize(
        call: ApplicationCall,
        requiredRoles: List<UserRole>,
        block: suspend () -> T,
    ) {
        return authorization.authorize(call, requiredRoles, block)
    }

    protected suspend fun fromUid(call: ApplicationCall): UUID {
        return authorization.getUserId(call)
    }

    fun uid(call: ApplicationCall): String {
        return call.principal<FirebasePrincipal>()?.uid ?: throw UserException.Unauthorized("Unauthorized Firebase user")
    }
}
