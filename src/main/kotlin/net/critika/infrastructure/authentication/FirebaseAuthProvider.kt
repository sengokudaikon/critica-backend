package net.critika.infrastructure.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthProvider(config: FirebaseConfig) : AuthenticationProvider(config) {
    val authHeader: (ApplicationCall) -> HttpAuthHeader? = config.authHeader
    private val authFunction = config.firebaseAuthenticationFunction
    val FirebaseJWTAuthKey: String = "FirebaseAuth"
    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val token = authHeader(context.call)

        if (token == null) {
            context.challenge(
                FirebaseJWTAuthKey,
                AuthenticationFailedCause.InvalidCredentials,
            ) { challengeFunc, call ->
                challengeFunc.complete()
                call.respond(
                    UnauthorizedResponse(
                        HttpAuthHeader.bearerAuthChallenge(
                            scheme = "Bearer",
                            realm = "firebase",
                        ),
                    ),
                )
            }
            return
        }

        try {
            val principal = verifyFirebaseIdToken(context.call, token, authFunction)

            if (principal != null) {
                context.principal(principal)
            }
        } catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            context.error(FirebaseJWTAuthKey, AuthenticationFailedCause.Error(message))
        }
    }

    suspend fun verifyFirebaseIdToken(
        call: ApplicationCall,
        authHeader: HttpAuthHeader,
        tokenData: suspend ApplicationCall.(FirebaseToken) -> Principal?,
    ): Principal? {
        val token: FirebaseToken = try {
            if (authHeader.authScheme == "Bearer" && authHeader is HttpAuthHeader.Single) {
                withContext(Dispatchers.IO) {
                    FirebaseAuth.getInstance().verifyIdToken(authHeader.blob)
                }
            } else {
                null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        } ?: return null
        return tokenData(call, token)
    }
}
